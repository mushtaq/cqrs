package account

import commons.Messages

object AccountRegion {

  val Name = "Account"

  sealed trait Command extends Messages.Command {
    def requestId: String

    def id: String = requestId
  }

  case class CreateAccount(requestId: String, balance: Double, primaryCustomerId: String, jointCustomerId: Option[String]) extends Command

  case class AccountCreated(requestId: String) extends Command

}
