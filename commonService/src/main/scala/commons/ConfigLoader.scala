package commons

import java.net.{InetAddress, InetSocketAddress, ServerSocket}

import com.typesafe.config._
import commons.ConfigObjectExtensions.RichConfig

import scala.util.{Failure, Success, Try}

class ConfigLoader(params: Params) {

  def load() = config.withFallback(extraConfig).resolve()

  lazy val ClusterName = config.getString("cluster-name")

  lazy val config = ConfigFactory.load(
    params.env,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )

  def extraConfig = ConfigFactory.empty().withPair("hostname", privateIp)

  def privateIp = params.env match {
    case "prod" => InetAddress.getLocalHost.getHostAddress
    case _      => loopbackHostname
  }

  def loopbackHostname = {
    val port = config.getInt("port")
    val socket = new ServerSocket()

    def loop(x: Int): String = {
      if (x > 10) {
        throw new RuntimeException("No hostname available for biding the server")
      }
      else {
        val hostname = s"127.0.0.$x"
        val address = new InetSocketAddress(hostname, port)
        Try(socket.bind(address)) match {
          case Success(_) => hostname
          case Failure(_) => loop(x + 1)
        }
      }
    }

    val hostname = loop(1)
    socket.close()
    hostname
  }

}
