package commons

import java.util.Optional

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}

class Assembly(params: Params, serviceName: String) {

  val configLoader = new ConfigLoader(params, serviceName)
  val system = ActorSystem(configLoader.ClusterName, configLoader.load())
  val runtime = new ActorRuntime(system)

  def startShard(props: Props) = ClusterSharding(system).start(
    typeName = serviceName,
    entityProps = props,
    settings = ClusterShardingSettings(system).withRole(serviceName),
    messageExtractor = Cqrs.messageExtractor
  )

  def startProxy(shardName: String) = ClusterSharding(system).startProxy(
    typeName = shardName,
    role = Optional.of(shardName),
    messageExtractor = Cqrs.messageExtractor
  )

  def startSingleton(props: Props) = system.actorOf(
    props = ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole(serviceName)
    ),
    name = serviceName
  )

  def getSingleton(singletonName: String) = system.actorOf(
    props = ClusterSingletonProxy.props(
      s"/user/$singletonName",
      ClusterSingletonProxySettings(system).withRole(singletonName)
    ),
    name = s"$singletonName-Proxy"
  )

}
