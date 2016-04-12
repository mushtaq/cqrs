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
    val accountId: String = holder.ssn + "-" + System.nanoTime()
    val account = Account(accountId, balance, holder.ssn, jointHolder.map(_.ssn))
    val holders = List(holder) ++ jointHolder
  }
}
