package gateway

import commons.Messages
import commons.Messages.{Account, Customer}

object GatewayRegion {

  val Name = "Gateway"

  sealed trait Command extends Messages.Command {
    def accountId: String

    def id: String = accountId
  }

  case class CreateAccountRequest(balance: Double, holder: Customer, jointHolder: Option[Customer]) extends Command {
    def accountId: String = holder.ssn + "-" + System.nanoTime()
    def account = Account(accountId, balance, holder.ssn, jointHolder.map(_.ssn))
    def holders = List(holder) ++ jointHolder
  }
}
