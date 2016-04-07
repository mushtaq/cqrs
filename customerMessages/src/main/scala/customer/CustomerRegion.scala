package customer

import commons.Messages
import commons.Messages.Customer

object CustomerRegion {

  val Name = "Customer"

  sealed trait Command extends Messages.Command {
    def ssn: String

    def id: String = ssn
  }

  case class CreateCustomer(ssn: String, customer: Customer) extends Command

  case class CustomerCreated(customer: Customer)
  case class CustomerCreationFailed(customer: Customer, reason: String)
}
