package commons

import akka.cluster.sharding.ShardRegion
import commons.Messages.Command

object Cqrs {

  val maxNumberOfNodes = 3

  val messageExtractor = new ShardRegion.HashCodeMessageExtractor(maxNumberOfNodes * 10) {
    def entityId(message: Any): String = message match {
      case x: Command => x.id
    }
  }

}
