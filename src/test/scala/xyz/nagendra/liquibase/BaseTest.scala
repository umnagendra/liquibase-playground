package xyz.nagendra.liquibase

import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.sql.{ Connection, DriverManager }

trait BaseTest extends AnyFunSuite with ForAllTestContainer with BeforeAndAfterAll with Constants {

  // STEP 1: Create a PostgreSQL testcontainer
  override val container: PostgreSQLContainer =
    PostgreSQLContainer(dockerImageNameOverride = "postgres:13.5",
                        databaseName = DB_NAME,
                        username = DB_USER,
                        password = DB_PASSWORD
    )

  // STEP 2: start the testcontainer (waits until it is started)
  container.start()

  // STEP 3: classload JDBC driver
  val _ = Class.forName(container.driverClassName)

  // STEP 4: get JDBC connection
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
}
