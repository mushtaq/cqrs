package customer

import commons.Messages
import commons.Messages.Customer

object CustomerRegion {

  val Name = "Customer"

  sealed trait Command extends Messages.Command {
    def ssn: String

    def id: String = ssn
  }

  case class CreateCustomer(ssn: String, requestId: String, customer: Customer) extends Command

  case class CustomerCreated(requestId: String, ssn: String) extends Command

  case class CustomerCreationFailed(requestId: String, ssn: String, reason: String) extends Command

}
