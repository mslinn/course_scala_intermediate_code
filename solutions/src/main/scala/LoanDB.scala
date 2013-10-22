import collection.immutable.Map
import java.sql.{Connection, DriverManager}
import scala.util.{Failure, Success, Try}

trait Closeable {
  //type Closer = AutoCloseable // this works fine, but there is a more flexible approach:
  /** Alias for structural type that has a method called close which does not accept parameters and returns nothing */
  type Closer = { def close(): Unit }

  /** Modified to handle Closeable or AutoCloseable by using a structural type */
  def withCloseable[C <: Closer, T](factory: => C)(operation: C => T): Try[T] = {
    val closeable = factory
    try {
      val result: T = operation(closeable)
      closeable.close()
      Success(result)
    } catch {
      case throwable: Throwable =>
        try { closeable.close() } catch { case _: Throwable => }
        println(throwable.toString)
        Failure(throwable)
    }
  }
}

trait DBOps {
  def connectTable(url: String, userName: String="", password: String=""): Connection = {
    val conn = connection(url, userName, password)
    createTable(conn, "person", "id integer, name string, age integer")
  }

  def connection(url: String, userName: String="", password: String=""): Connection = {
    Class.forName("org.sqlite.JDBC")
    DriverManager.getConnection(url, userName, password)
  }

  def createTable(connection: Connection, tableName: String, creationStatement: String): Connection = {
    val statement = connection.createStatement
    statement.setQueryTimeout(30)
    statement.executeUpdate(s"drop table if exists $tableName")
    statement.executeUpdate(s"create table $tableName ($creationStatement)")
    connection
  }

  def insert(conn: Connection, tableName: String, nameValueMap: Map[String, Any]) = {
    val keys = nameValueMap.keys.toList // nameValueMap.keys is an iterable, which can only be traversed once
    val names = keys.mkString(", ")
    val placeholders = keys.map { _ => "?" }.mkString(", ")
    val stmtString = s"insert into $tableName ($names) values ($placeholders)"
    val stmt = conn.prepareStatement(stmtString)
    nameValueMap.values.zipWithIndex foreach { case (value, i) =>
      stmt.setObject(i+1, value)
    }
    stmt.executeUpdate()
  }
}

trait AdvancedDBOps extends DBOps {
  override def connectTable(url: String, userName: String="", password: String=""): Connection = {
    val conn = connection(url, userName, password)
    val fieldMap = Map("id" -> "integer", "name" -> "string", "age" -> "integer")
    createTable(conn, "person", fieldMap)
  }

  def createTable(connection: Connection, tableName: String, fields: Map[String, String]): Connection = {
    val statement = connection.createStatement()
    statement.setQueryTimeout(30)
    statement.executeUpdate(s"drop table if exists $tableName")
    val creationStatement = fields.keys.map { key => s"$key ${fields(key)}"}.mkString(", ")
    statement.executeUpdate(s"create table $tableName ($creationStatement)")
    connection
  }
}

/** Mix in AdvancedDBOps instead of DBOps to alternate the implementation */
object LoanDB extends App with Closeable with DBOps {
  withCloseable(connectTable("jdbc:sqlite:person.db")) { conn: Connection =>
    insert(conn, "person", Map("id" -> 1, "name" -> "Fred Flintsone",  "age" -> 400002))
    insert(conn, "person", Map("id" -> 2, "name" -> "Wilma Flintsone", "age" -> 400001))
    insert(conn, "person", Map("id" -> 3, "name" -> "Barney Rubble",   "age" -> 400004))
    insert(conn, "person", Map("id" -> 4, "name" -> "Betty Rubble",    "age" -> 400003))

    val stmt2 = conn.prepareStatement("select * from person")
    withCloseable(stmt2.executeQuery) { resultSet =>
      val columnCount: Int = resultSet.getMetaData.getColumnCount
      do {
        val x = (1 to columnCount).map(resultSet.getObject).mkString(", ")
        println(x)
      } while (resultSet.next)
    }
  }
}
