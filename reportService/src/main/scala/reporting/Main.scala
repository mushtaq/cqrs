package reporting

import caseapp._
import commons.Params

object Main extends AppOf[ReportingMain] {
  def parser = default
}

case class ReportingMain(params: Params) extends App {

  val journalFactory = new JournalFactory(params, "customer")
  val readJournal = journalFactory.readJournal

  val ids = readJournal.allPersistenceIds()
  val events = readJournal.eventsByPersistenceId("Customer-111", 0, Long.MaxValue)


  import journalFactory.runtime._

  ids.runForeach { id => println("PersistenceId: " + id) }
  events.runForeach { e => println("Event: " + e) }
}
