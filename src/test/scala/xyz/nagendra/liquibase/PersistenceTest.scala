package xyz.nagendra.liquibase

import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.sql.{ Connection, DriverManager }
import java.util.UUID

class PersistenceTest extends AnyFunSuite with ForAllTestContainer with BeforeAndAfterAll with Constants {

  // Create a PostgreSQL testcontainer
  override val container: PostgreSQLContainer =
    PostgreSQLContainer(dockerImageNameOverride = "postgres:13.5",
                        databaseName = DB_NAME,
                        username = DB_USER,
                        password = DB_PASSWORD
    )

  // classload JDBC driver
  val _ = Class.forName(container.driverClassName)

  // start the testcontainer (waits until it is started)
  container.start()

  // get JDBC connection
  val connection: Connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

  override def beforeAll(): Unit = {
    // create liquibase
    val liquibase = createLiquibase(connection, CHANGELOG_FILE)

    try {
      // apply changelogs and update DB to latest schema
      liquibase.update("")

      // validate that the tables are actually created properly
      val result = connection.prepareStatement("select count(*) from agent").executeQuery()
      assert(result.next() === true, "Expected ONE result in result set")
      assert(result.getInt(1) === 10, "Agent table does not have the right count of rows")
    } finally liquibase.forceReleaseLocks()
  }

  private def createLiquibase(connection: Connection, changelogFilePath: String): Liquibase = {
    val database         = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
    val classLoader      = classOf[PersistenceTest].getClassLoader
    val resourceAccessor = new ClassLoaderResourceAccessor(classLoader)
    new Liquibase(changelogFilePath, resourceAccessor, database)
  }

  test("Add a contact into DB") {
    val contactId         = UUID.randomUUID()
    val contactName       = "John Doe"
    val selectContactById = connection.prepareStatement(s"select * from contact where id = ?")
    selectContactById.setObject(1, contactId)

    println(s"JDBC URL = ${container.jdbcUrl}")

    // BEFORE: Validate that no such contact exists already
    println(s"BEFORE - Making query: $selectContactById")
    val beforeResult = selectContactById.executeQuery()
    assert(beforeResult.next() === false, "Expected ZERO rows to be fetched")

    // Invoke persistence utility to add a new contact into DB
    val persistence = new Persistence(container.jdbcUrl, container.username, container.password)
    assert(persistence.insertContact(contactId, contactName))

    // AFTER: Validate that the new contact is indeed persisted in DB
    println(s"AFTER - Making query: $selectContactById")
    val afterResult = selectContactById.executeQuery()
    assert(afterResult.next() === true, "Expected 1 row to be fetched")
    assert(afterResult.getString(2) === contactName, s"Expected 2nd column value to be '$contactName'")
  }
}
