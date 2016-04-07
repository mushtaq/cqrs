package commons

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}

class Assembly(params: Params, role: String) {

  val configLoader = new ConfigLoader(params, role)
  val system = ActorSystem(configLoader.ClusterName, configLoader.load())
  val runtime = new ActorRuntime(system)

  def startShard(props: Props) = ClusterSharding(system).start(
    typeName = role,
    entityProps = props,
    settings = ClusterShardingSettings(system).withRole(role),
    extractEntityId = Cqrs.identExtractor,
    extractShardId = Cqrs.shardResolver
  )

  def startProxy(name: String) = ClusterSharding(system).startProxy(
    typeName = name,
    role = Some(name),
    extractEntityId = Cqrs.identExtractor,
    extractShardId = Cqrs.shardResolver
  )

  def startSingleton(props: Props) = system.actorOf(
    props = ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole(role)
    ),
    name = role
  )

  def getSingleton(name: String) = system.actorOf(
    props = ClusterSingletonProxy.props(
      s"/user/$name",
      ClusterSingletonProxySettings(system).withRole(name)
    ),
    name = s"$name-Proxy"
  )

}
