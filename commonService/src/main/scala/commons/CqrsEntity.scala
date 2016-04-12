package commons

import akka.actor.{PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentActor

import scala.concurrent.duration._

abstract class CqrsEntity extends PersistentActor {
  def persistenceId: String = self.path.parent.name + "-" + self.path.name

  override def preStart(): Unit = {
    context.setReceiveTimeout(15.seconds)
    println(s">>>>> creating $self")
  }

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout =>
      println(s"*** passivating due to unhandled timeout: $self")
      context.parent ! Passivate(PoisonPill)
    case _              =>
      println(s"^^^^ unhandled message: $message")
      super.unhandled(message)
  }
}
