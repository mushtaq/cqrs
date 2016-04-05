package customer

import akka.actor.{Actor, ActorRef}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.pattern.{ask, pipe}
import akka.persistence.PersistentActor
import akka.util.Timeout
import credit_history.CreditHistoryRegion
import credit_history.CreditHistoryRegion.{BadCreditHistory, GoodCreditHistory, ValidateCreditHistory}
import customer.CustomerRegion.{CreateCustomer, CustomerCreated, CustomerCreationFailed}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class CustomerEntity extends PersistentActor {

  val creditHistoryProxy = context.actorOf(ClusterSingletonProxy.props(
    CreditHistoryRegion.Name,
    ClusterSingletonProxySettings(context.system)
  ))

  implicit val timeout: Timeout = Timeout(10.seconds)
  import context.dispatcher

  def persistenceId: String = self.path.parent.name + "-" + self.path.name

  def receiveCommand: Receive = {
    case msg @ CreateCustomer(ssn, requestId, c) =>
      val response: Future[Any] = creditHistoryProxy ? ValidateCreditHistory(ssn)
      response pipeTo self
      context.become(validationRequested(sender(), msg))
  }

  def validationRequested(replyTo: ActorRef, createRequest: CreateCustomer): Receive = {
    case GoodCreditHistory =>
      replyTo ! CustomerCreated(createRequest.requestId, createRequest.ssn)
      context.become(customerCreated)
    case BadCreditHistory  =>
      replyTo ! CustomerCreationFailed(createRequest.requestId, createRequest.ssn, "bad credit history")
      context.become(receiveCommand)
  }

  def customerCreated: Receive = {
    case msg @ CreateCustomer(ssn, requestId, c) =>
      sender() ! CustomerCreated(requestId, ssn)
  }

  def receiveRecover: Receive = Actor.emptyBehavior

}
