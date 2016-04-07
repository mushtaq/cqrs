package credit_history

import akka.actor.Props
import caseapp._
import commons.{Assembly, Params}

object Main extends AppOf[CreditHistoryMain] {
  def parser = default
}

case class CreditHistoryMain(params: Params) extends App {
  new Assembly(params, CreditHistoryRegion.Name).startSingleton(Props(new CreditHistoryValidator))
}
