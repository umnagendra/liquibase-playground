package xyz.nagendra.liquibase

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class Persistence(jdbcUrl: String, dbUser: String, dbPassword: String) {
  val db = Database.forURL(url = jdbcUrl, user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")

  import Tables.contact

  def insertContact(id: UUID, name: String): Boolean = {
    val insertAction: DBIO[Option[Int]] =
      contact ++= Seq((id, name, Timestamp.from(Instant.now()), 22, "INSERTED FROM CODE", "SOME VALUE"))
    Await.result(db.run(insertAction), 30.seconds) match {
      case Some(_) => true
      case None    => false
    }
  }
}

object Tables {

  val contact = TableQuery[Contact]

  class Contact(tag: Tag) extends Table[(UUID, String, Timestamp, Int, String, String)](tag, "contact") {
    override def * : ProvenShape[(UUID, String, Timestamp, Int, String, String)] = (id, col1, col2, col3, col4, col5)

    def id: Rep[UUID]        = column[UUID]("id", O.PrimaryKey)
    def col1: Rep[String]    = column[String]("col1")
    def col2: Rep[Timestamp] = column[Timestamp]("col2")
    def col3: Rep[Int]       = column[Int]("col3")
    def col4: Rep[String]    = column[String]("col4")
    def col5: Rep[String]    = column[String]("col5")
  }
}
