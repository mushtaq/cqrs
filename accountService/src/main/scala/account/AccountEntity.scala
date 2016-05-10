package account

import account.AccountRegion.{AccountCreated, CreateAccount}
import akka.actor.Actor
import commons.CqrsEntity
import commons.Messages.Account

class AccountEntity extends CqrsEntity {

  def prefix: String = AccountRegion.Name

  var state: Account = null

  def receiveCommand: Receive = {
    case CreateAccount(accountId, account) =>
      state = account
      sender() ! AccountCreated(account)
      context.become(accountCreated)
  }

  def accountCreated: Receive = {
    case CreateAccount(accountId, _) =>
      sender() ! AccountCreated(state)
  }

  def receiveRecover: Receive = Actor.emptyBehavior

}
