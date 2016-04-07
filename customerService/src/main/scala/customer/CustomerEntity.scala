package customer

import akka.actor.{Actor, ActorRef}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import commons.CqrsEntity
import commons.Messages.Customer
import credit_history.CreditHistoryRegion.{BadCreditHistory, GoodCreditHistory, ValidateCreditHistory}
import customer.CustomerRegion.{CreateCustomer, CustomerCreated, CustomerCreationFailed}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class CustomerEntity(creditHistoryProxy: ActorRef) extends CqrsEntity {

  implicit val timeout: Timeout = Timeout(10.seconds)
  import context.dispatcher

  def receiveCommand: Receive = {
    case msg @ CreateCustomer(ssn, c) =>
      val response: Future[Any] = creditHistoryProxy ? ValidateCreditHistory(ssn)
      response pipeTo self
      context.become(validationRequested(sender(), msg.customer))
  }

  def validationRequested(replyTo: ActorRef, customer: Customer): Receive = {
    case GoodCreditHistory =>
      replyTo ! CustomerCreated(customer)
      context.become(customerCreated)
    case BadCreditHistory  =>
      replyTo ! CustomerCreationFailed(customer, "bad credit history")
      context.become(receiveCommand)
  }

  def customerCreated: Receive = {
    case msg @ CreateCustomer(ssn, customer) =>
      sender() ! CustomerCreated(customer)
  }

  def receiveRecover: Receive = Actor.emptyBehavior

}
