package account

import account.AccountRegion.{AccountCreated, CreateAccount}
import akka.actor.Actor
import commons.CqrsEntity
import commons.Messages.Account

class AccountEntity extends CqrsEntity {

  def receiveCommand: Receive = {
    case CreateAccount(accountId, account) =>
      sender() ! AccountCreated(account)
      context.become(accountCreated(account))
  }

  def accountCreated(account: Account): Receive = {
    case CreateAccount(accountId, _) =>
      sender() ! AccountCreated(account)
  }

  def receiveRecover: Receive = Actor.emptyBehavior

}
