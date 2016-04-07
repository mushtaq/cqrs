package account

import commons.Messages
import commons.Messages.Account

object AccountRegion {

  val Name = "Account"

  sealed trait Command extends Messages.Command {
    def accountId: String

    def id: String = accountId
  }

  case class CreateAccount(accountId: String, account: Account) extends Command

  case class AccountCreated(account: Account)
}
