package gateway

import account.AccountRegion.{AccountCreated, CreateAccount}
import akka.actor.{Actor, ActorRef, PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import commons.CqrsEntity
import commons.Messages.{Account, Customer}
import customer.CustomerRegion.{CreateCustomer, CustomerCreated, CustomerCreationFailed}
import gateway.GatewayRegion.CreateAccountRequest

class RequestEntity(customerRegion: ActorRef, accountRegion: ActorRef) extends CqrsEntity {

  var pendingCustomers = List.empty[Customer]
  var account: Account = null

  def receiveCommand: Receive = {
    case msg: CreateAccountRequest =>
      account = msg.account
      pendingCustomers = msg.holders
      createCustomers()
      context.become(awaitCustomerCreation)
  }

  def awaitCustomerCreation: Receive = {
    case CustomerCreated(customer)                =>
      pendingCustomers = pendingCustomers.filterNot(_.ssn == customer.ssn)
      if (pendingCustomers.isEmpty) {
        createAccount()
        context.become(awaitAccountCreation)
      }
    case CustomerCreationFailed(customer, reason) =>
      println(s"customer creation failed for customer=$customer, account=$account, reason=$reason")
      context.parent ! Passivate(PoisonPill)
    case ReceiveTimeout                           =>
      createCustomers()
  }

  def awaitAccountCreation: Receive = {
    case AccountCreated(_) =>
      println(s"account created for account:$account")
      context.parent ! Passivate(PoisonPill)
    case ReceiveTimeout    =>
      createAccount()
  }

  def createAccount(): Unit = {
    accountRegion ! CreateAccount(account.accountId, account)
  }

  def createCustomers(): Unit = {
    pendingCustomers.foreach(customer => customerRegion ! CreateCustomer(customer.ssn, customer))
  }

  def receiveRecover: Receive = Actor.emptyBehavior
}
