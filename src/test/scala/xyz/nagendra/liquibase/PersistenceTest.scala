package xyz.nagendra.liquibase

import java.util.UUID

class PersistenceTest extends BaseTest {

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
