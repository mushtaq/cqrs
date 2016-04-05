package gateway

import commons.Messages
import commons.Messages.Customer

object GatewayRegion {

  val Name = "Gateway"

  sealed trait Command extends Messages.Command {
    def requestId: String

    def id: String = requestId
  }

  case class CreateAccountRequest(balance: Double, primaryHolder: Customer, jointHolder: Option[Customer]) extends Command {
    def requestId: String = primaryHolder.ssn + "-" + jointHolder.map(_.ssn).getOrElse("none") + "-" + System.nanoTime()
  }
}
