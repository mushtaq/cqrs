package credit_history

import akka.actor.Actor
import credit_history.CreditHistoryRegion.{BadCreditHistory, GoodCreditHistory, ValidateCreditHistory}

class CreditHistoryValidator extends Actor {
  def receive: Receive = {
    case ValidateCreditHistory(ssn) if ssn.endsWith("0") => sender() ! BadCreditHistory
    case ValidateCreditHistory(ssn)                      => sender() ! GoodCreditHistory
  }
}
