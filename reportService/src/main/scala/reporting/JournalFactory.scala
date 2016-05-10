package reporting

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigResolveOptions}
import commons.ConfigObjectExtensions.RichConfig
import commons.{ActorRuntime, Params}

class JournalFactory(params: Params, keyspace: String) {

  def loadConfig = {
    lazy val config = ConfigFactory.load(
      params.env,
      ConfigParseOptions.defaults(),
      ConfigResolveOptions.defaults().setAllowUnresolved(true)
    )

    val extraConfig = ConfigFactory.empty().withPair("keyspace", keyspace)

    config.withFallback(extraConfig).resolve()
  }

  val system = ActorSystem("Reporting", loadConfig)
  val runtime = new ActorRuntime(system)

  val readJournal =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](
      CassandraReadJournal.Identifier
    )
}
