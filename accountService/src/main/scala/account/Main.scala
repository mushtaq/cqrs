package account

import akka.actor.Props
import caseapp._
import commons.{Assembly, Params}

object Main extends AppOf[AccountMain] {
  def parser = default
}

case class AccountMain(params: Params) extends App {
  new Assembly(params, AccountRegion.Name).startShard(Props(new AccountEntity))
}
