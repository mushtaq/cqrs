package customer

import akka.actor.ActorRef
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import commons.CqrsEntity
import commons.Messages.Customer
import credit_history.CreditHistoryRegion.{BadCreditHistory, GoodCreditHistory, ValidateCreditHistory}
import customer.CustomerRegion._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class CustomerEntity(creditHistoryProxy: ActorRef) extends CqrsEntity {

  def prefix: String = CustomerRegion.Name

  implicit val timeout: Timeout = Timeout(10.seconds)
  import context.dispatcher

  var state: Customer = null

  def receiveRecover: Receive = {
    case CustomerCreated(c) =>
      state = c
      context.become(customerCreated)
    case AddressUpdated(address) =>
      state = state.copy(address = address)
  }

  def receiveCommand: Receive = {
    case CreateCustomer(ssn, c) =>
      val response: Future[Any] = creditHistoryProxy ? ValidateCreditHistory(ssn)
      response pipeTo self
      context.become(validationRequested(sender(), c))
  }

  def validationRequested(replyTo: ActorRef, customer: Customer): Receive = {
    case GoodCreditHistory => persistAsync(CustomerCreated(customer)) { evt =>
      replyTo ! evt
      this.state = customer
      context.become(customerCreated)
    }

    case BadCreditHistory  =>
      replyTo ! CustomerCreationFailed(customer, "bad credit history")
      context.become(receiveCommand)
  }

  def customerCreated: Receive = {
    case CreateCustomer(_, _) =>
      sender() ! CustomerCreated(state)
    case UpdateAddress(_, address) => persistAsync(AddressUpdated(address)) { evt =>
      sender() ! evt
      state = state.copy(address = address)
    }

  }

}
