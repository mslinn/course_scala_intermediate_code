package solutions

trait DBOps extends Closeable {
  import java.sql.{Connection, DriverManager, ResultSet, Statement}
  import java.util.concurrent.atomic.AtomicReference

import scala.collection.immutable.Map

  Class.forName("org.sqlite.JDBC")

  def connectTable(url: String, userName: String="", password: String=""): Connection = {
    implicit val conn = connection(url, userName, password)
    createTable("person", "id integer, name string, age integer")
  }

  def connection(url: String, userName: String="", password: String=""): Connection =
    DriverManager.getConnection(url, userName, password)

  def createTable(tableName: String, creationStatement: String)(implicit connection: Connection): Connection = {
    val statement = connection.createStatement
    statement.setQueryTimeout(30)
    statement.executeUpdate(s"drop table if exists $tableName")
    statement.executeUpdate(s"create table $tableName ($creationStatement)")
    connection
  }

  /** Creates a new record for the specified table from name/value pairs  */
  def insert(tableName: String, nameValueMap: Map[String, Any])(implicit conn: Connection): Int = {
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

  /** Required to support statement.cancel() */
  private val maybeCurrentStatement: AtomicReference[Option[Statement]] = new AtomicReference(None)

  /** Can be run from another thread than execute() */
  def cancel(): Unit = maybeCurrentStatement.getAndSet(None).foreach(_.cancel())

  /** Executes simple queries, which can be cancelled */
  def execute(statementString: String)(body: ResultSet => Any)(implicit conn: Connection): Unit = {
    withCloseable(conn.createStatement) { statement =>
      maybeCurrentStatement.set(Some(statement))
      val rs = statement.executeQuery(statementString)
      withCloseable(rs) { resultSet =>
        body(resultSet)
      }
      maybeCurrentStatement.set(None)
    }
  }
  ()
}
