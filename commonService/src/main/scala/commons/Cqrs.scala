package commons

import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import commons.Messages.Command

object Cqrs {

  val maxNumberOfNodes = 3
  val shardsPerNode = 10
  val numberOfShards = maxNumberOfNodes * shardsPerNode

  val messageExtractor = new HashCodeMessageExtractor(numberOfShards) {
    def entityId(message: Any): String = message match {
      case x: Command => x.id
      case x          => throw new RuntimeException(s"unsupported command without id: $x")
    }
  }

}
