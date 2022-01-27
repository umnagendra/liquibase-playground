package xyz.nagendra.liquibase

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import java.sql.{ Connection, DriverManager }
import scala.util.{ Failure, Success, Try }

trait Constants {
  val DB_HOST        = "localhost"
  val DB_PORT        = 5432
  val DB_NAME        = "core"
  val DB_USER        = "root"
  val DB_PASSWORD    = "root"
  val JDBC_URL       = s"jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"
  val CHANGELOG_FILE = "db/db.changelog-master.yaml"
}

trait MainTrait {

  def getJDBCConnection(jdbcUrl: String, user: String, password: String): Connection = {
    val _ = classOf[org.postgresql.Driver] // classload JDBC driver
    DriverManager.getConnection(jdbcUrl, user, password)
  }

  def updateSchema(connection: Connection, changelogFilePath: String): Unit = {
    println(
      s"Updating DB schema from $changelogFilePath onto DB connection to ${connection.getMetaData.getDatabaseProductName}(${connection.getMetaData.getDatabaseProductVersion}) ..."
    )
    val liquibase = createLiquibase(connection, changelogFilePath)
    try liquibase.update("")
    catch {
      case e: Exception => throw e
    } finally {
      liquibase.forceReleaseLocks()
      connection.close()
    }
  }

  def createLiquibase(connection: Connection, changelogFilePath: String): Liquibase = {
    val database         = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
    val classLoader      = classOf[xyz.nagendra.liquibase.MainTrait].getClassLoader
    val resourceAccessor = new ClassLoaderResourceAccessor(classLoader)
    new Liquibase(changelogFilePath, resourceAccessor, database)
  }
}

object Main extends App with Constants with MainTrait {
  val connection: Connection = getJDBCConnection(JDBC_URL, DB_USER, DB_PASSWORD)
  Try {
    updateSchema(connection, CHANGELOG_FILE)
  } match {
    case Failure(exception) =>
      println("Failed to update schema.")
      exception.printStackTrace()
    case Success(_) => println("Successfully updated schema!")
  }
}
