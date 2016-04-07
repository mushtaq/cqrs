package gateway

import account.AccountRegion
import akka.actor.Props
import caseapp._
import commons.{Assembly, Params}
import customer.CustomerRegion

object Main extends AppOf[GatewayMain] {
  def parser = default
}

case class GatewayMain(params: Params) extends App {

  val assembly = new Assembly(params, GatewayRegion.Name)

  val customerRegion = assembly.startProxy(CustomerRegion.Name)
  val accountRegion = assembly.startProxy(AccountRegion.Name)

  assembly.startShard(Props(new RequestEntity(customerRegion, accountRegion)))
}
