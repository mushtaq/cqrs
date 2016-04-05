package commons

object Messages {
  trait Command {
    def id: String
  }

  case class Address(street: String, city: String, pin: Int)

  case class Customer(ssn: String, fName: String, lName: String, address: Address)
}
