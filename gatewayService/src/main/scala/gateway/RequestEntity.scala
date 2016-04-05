package gateway

import account.AccountRegion
import account.AccountRegion.{AccountCreated, CreateAccount}
import akka.actor.{Actor, PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentActor
import commons.Messages.Customer
import customer.CustomerRegion
import customer.CustomerRegion.{CreateCustomer, CustomerCreated, CustomerCreationFailed}
import gateway.GatewayRegion.CreateAccountRequest

import scala.concurrent.duration._

class RequestEntity extends PersistentActor {

  val customerRegion = ClusterSharding(context.system).shardRegion(CustomerRegion.Name)
  val accountRegion = ClusterSharding(context.system).shardRegion(AccountRegion.Name)

  context.setReceiveTimeout(1.minute)

  def persistenceId: String = self.path.parent.name + "-" + self.path.name

  def receiveCommand: Receive = {
    case msg @ CreateAccountRequest(balance, primaryHolder, jointHolder) =>
      val pendingCustomers = List(primaryHolder) ++ jointHolder
      createCustomers(pendingCustomers, msg)
      context.become(awaitCustomerCreation(pendingCustomers, msg))
  }

  def awaitCustomerCreation(pendingCustomers: List[Customer], request: CreateAccountRequest): Receive = {
    case CustomerCreated(requestId, ssn)                =>
      val pendingCustomers1 = pendingCustomers.filterNot(_.ssn == ssn)
      if (pendingCustomers1.isEmpty) {
        createAccount(request)
        context.become(awaitAccountCreation(request))
      } else {
        context.become(awaitCustomerCreation(pendingCustomers1, request))
      }
    case CustomerCreationFailed(requestId, ssn, reason) =>
      println(s"customer creation failed for customer=$ssn, request=$request, reason=$reason")
      passivate()
    case ReceiveTimeout                                 =>
      createCustomers(pendingCustomers, request)
  }

  def awaitAccountCreation(request: CreateAccountRequest): Receive = {
    case AccountCreated(requestId) =>
      println(s"account created for request:$request")
      passivate()
    case ReceiveTimeout            =>
      createAccount(request)
  }

  def createAccount(request: CreateAccountRequest): Unit = {
    accountRegion ! CreateAccount(request.requestId, request.balance, request.primaryHolder.ssn, request.jointHolder.map(_.ssn))
  }

  def createCustomers(pendingCustomers: List[Customer], request: CreateAccountRequest): Unit = {
    pendingCustomers.foreach(c => customerRegion ! CreateCustomer(c.ssn, request.requestId, c))
  }

  def passivate() = {
    context.parent ! Passivate(PoisonPill)
  }

  def receiveRecover: Receive = Actor.emptyBehavior
}
