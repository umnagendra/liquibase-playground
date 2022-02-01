package xyz.nagendra.liquibase.custom

import com.typesafe.scalalogging.LazyLogging
import liquibase.change.custom.{ CustomTaskChange, CustomTaskRollback }
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.{ CustomChangeException, ValidationErrors }
import liquibase.resource.ResourceAccessor

import java.net._
import java.sql.PreparedStatement

class ExampleCustomSchemaChange extends CustomTaskChange with CustomTaskRollback with LazyLogging {

  override def execute(database: Database): Unit = {
    logger.debug("Inside ExampleCustomSchemaChange#execute() ...")
    updateCol4(database, s"IP Address: ${InetAddress.getLocalHost.getHostAddress}")
  }

  override def rollback(database: Database): Unit = {
    logger.debug("Inside ExampleCustomSchemaChange#rollback() ...")
    updateCol4(database, "ROLLED-BACK")
  }

  private def updateCol4(database: Database, value: String): Unit = {
    val connection: JdbcConnection = database.getConnection.asInstanceOf[JdbcConnection]

    val updateAgentStmt                      = "update agent set col4 = ?"
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(updateAgentStmt)
      preparedStatement.setString(1, value)
      val numRowsUpdated = preparedStatement.executeUpdate
      logger.info(s"Number of rows updated is $numRowsUpdated")
    } catch {
      case e: Exception => throw new CustomChangeException("Exception trying to update col4", e)
    } finally preparedStatement.close()
  }

  override def getConfirmationMessage: String = "****** CONFIRMATION - CUSTOM SCHEMA CHANGE IS EXECUTED !!! *****"

  override def setUp(): Unit = ()

  override def setFileOpener(resourceAccessor: ResourceAccessor): Unit = ()

  override def validate(database: Database): ValidationErrors = null
}
