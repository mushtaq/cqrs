package commons

import akka.actor.{PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentActor
import commons.Messages.{Get, State}

import scala.concurrent.duration._

abstract class CqrsEntity extends PersistentActor {
  def state: Any

  def prefix: String

  def persistenceId: String = prefix + "-" + self.path.name

  override def preStart(): Unit = {
    context.setReceiveTimeout(1.minute)
    println(s">>>>> creating $self")
  }

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout =>
      println(s"*** passivating due to unhandled timeout: $self")
      context.parent ! Passivate(PoisonPill)
    case Get(_)         =>
      sender() ! State(Option(state))
    case _              =>
      println(s"^^^^ unhandled message: $message")
      super.unhandled(message)
  }
}
