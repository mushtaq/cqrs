package client

import caseapp._
import commons.Messages.{Address, Customer}
import commons.{Assembly, Params}
import gateway.GatewayRegion
import gateway.GatewayRegion.CreateAccountRequest

object Main extends AppOf[ClientMain] {
  def parser = default
}

case class ClientMain(params: Params) extends App {
  val assembly = new Assembly(params, "client")

  val gatewayRegion = assembly.startProxy(GatewayRegion.Name)

  Iterator.from(1).foreach { id =>
    Thread.sleep(2000)
    gatewayRegion ! CreateAccountRequest(
      0, Customer(id.toString, "mushtaq", "ahmed", Address("baner", "pune", 21)), None
    )

  }
}
