package customer

import commons.Messages
import commons.Messages.{Address, Customer}

object CustomerRegion {

  val Name = "Customer"

  sealed trait Command extends Messages.Command {
    def ssn: String

    def id: String = ssn
  }

  case class CreateCustomer(ssn: String, customer: Customer) extends Command
  case class UpdateAddress(ssn: String, address: Address) extends Command

  case class CustomerCreated(customer: Customer)
  case class AddressUpdated(address: Address)
  case class CustomerCreationFailed(customer: Customer, reason: String)
}
