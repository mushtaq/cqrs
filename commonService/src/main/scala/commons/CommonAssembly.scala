package commons

import akka.actor.ActorSystem

class CommonAssembly(params: Params) {
  val configLoader = new ConfigLoader(params)
  val system = ActorSystem(configLoader.ClusterName, configLoader.load())
  val runtime = new ActorRuntime(system)
}
