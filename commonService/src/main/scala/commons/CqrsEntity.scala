package commons

import akka.actor.{PoisonPill, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentActor

import scala.concurrent.duration._

abstract class CqrsEntity extends PersistentActor {
  def persistenceId: String = self.path.parent.name + "-" + self.path.name

  context.setReceiveTimeout(1.minute)

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout => context.parent ! Passivate(PoisonPill)
    case _              => super.unhandled(message)
  }
}
