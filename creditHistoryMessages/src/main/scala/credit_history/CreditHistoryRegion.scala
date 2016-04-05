package credit_history

object CreditHistoryRegion {

  val Name = "CreditHistory"

  case class ValidateCreditHistory(ssn: String)

  case object GoodCreditHistory
  case object BadCreditHistory
}
