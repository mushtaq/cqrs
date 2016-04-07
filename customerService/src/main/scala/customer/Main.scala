package customer

import akka.actor.Props
import caseapp._
import commons.{Assembly, Params}
import credit_history.CreditHistoryRegion

object Main extends AppOf[CustomerMain] {
  def parser = default
}

case class CustomerMain(params: Params) extends App {

  val assembly = new Assembly(params, CustomerRegion.Name)

  val creditHistoryProxy = assembly.getSingleton(CreditHistoryRegion.Name)

  assembly.startShard(Props(new CustomerEntity(creditHistoryProxy)))
}
