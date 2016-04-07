package commons

import akka.cluster.sharding.ShardRegion
import commons.Messages.Command

object Cqrs {

  val numberOfNodes = 3

  val identExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.id, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: Command =>
      val numberOfShards = numberOfNodes * 10
      (cmd.id.hashCode.abs % numberOfShards).toString
  }

}
