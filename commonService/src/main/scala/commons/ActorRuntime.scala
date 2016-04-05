package commons

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class ActorRuntime(val system: ActorSystem) {
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()(system)
  implicit val timeout: Timeout = Timeout(10.seconds)

  def shutdown() = system.terminate()
}
