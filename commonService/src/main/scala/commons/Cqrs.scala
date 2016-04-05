package commons

import akka.cluster.sharding.ShardRegion
import commons.Messages.Command

object Cqrs {

  val identExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.id, cmd)
  }

  def shardResolver(numberOfNodes: Int): ShardRegion.ExtractShardId = {
    case cmd: Command =>
      val numberOfShards = numberOfNodes * 10
      (cmd.id.hashCode.abs % numberOfShards).toString
  }

}
