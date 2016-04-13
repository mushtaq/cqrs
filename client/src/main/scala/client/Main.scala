package client

import account.AccountRegion
import account.AccountRegion.CreateAccount
import akka.util.Timeout
import caseapp._
import commons.Messages.{Account, Address, Customer, Get}
import commons.{Assembly, Params}
import customer.CustomerRegion
import customer.CustomerRegion.{CreateCustomer, UpdateAddress}
import gateway.GatewayRegion
import gateway.GatewayRegion.CreateAccountRequest

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

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

object Client {
  val assembly = new Assembly(Params(), "client")

  import akka.pattern.ask
  implicit val timeout: Timeout = Timeout(10.seconds)

  val gatewayRegion = assembly.startProxy(GatewayRegion.Name)
  val customerRegion = assembly.startProxy(CustomerRegion.Name)
  val accountRegion = assembly.startProxy(AccountRegion.Name)

  def sendRequest(id: Int) = {
    val req = CreateAccountRequest(0, customer(id), None)
    gatewayRegion ! req
    println("****************")
    println(req.id)
  }

  def createCustomer(id: Int) = {
    customerRegion ! CreateCustomer(id.toString, customer(id))
  }

  def createAccount(id: Int) = {
    accountRegion ! CreateAccount(id.toString, account(id))
  }

  def getCustomer(id: Int) = customerRegion ? Get(id.toString)
  def getAccount(id: Int) = accountRegion ? Get(id.toString)
  def getRequest(id: Int) = gatewayRegion ? Get(id.toString)

  def updateAddress(id: Int, newPin: Int) = customerRegion ? UpdateAddress(id.toString, Address("new", "new", newPin))

  def customer(id: Int) = Customer(id.toString, "mushtaq", "ahmed", Address("baner", "pune", 21))
  def account(id: Int) = Account(id.toString, 100, "abc", Some("def"))

  def close = assembly.system.terminate()
}
