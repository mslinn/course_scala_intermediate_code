package solutions

trait DBOps extends Closeable {
  import java.sql.{Connection, DriverManager, ResultSet, Statement}
  import java.util.concurrent.atomic.AtomicReference
  import scala.collection.immutable.Map
  import scala.concurrent.{ExecutionContext, Future}
  import scala.util.Try

  Class.forName("org.sqlite.JDBC")

  def connectTable(url: String, userName: String="", password: String="")(implicit ec: ExecutionContext): Future[Connection] =
    connection(url, userName, password) flatMap {
      createTable("person", "id integer, name string, age integer")(_, ec)
    }

  def connection(url: String, userName: String="", password: String="")(implicit ec: ExecutionContext): Future[Connection] =
    Future(DriverManager.getConnection(url, userName, password))

  def createTable(tableName: String, creationStatement: String)(implicit connection: Connection, ec: ExecutionContext): Future[Connection] = Future {
    val statement = connection.createStatement
    statement.setQueryTimeout(30)
    statement.executeUpdate(s"drop table if exists $tableName")
    statement.executeUpdate(s"create table $tableName ($creationStatement)")
    connection
  }

  /** Creates a new record for the specified table from name/value pairs
    * @return Future[number of rows inserted] */
  def insert(tableName: String, nameValueMap: Map[String, Any])(implicit conn: Connection, ec: ExecutionContext): Future[Int] = Future {
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

  /** Executes simple queries, which can be cancelled
    * @return Future[Unit] or Exception */
  def execute(statementString: String)(body: ResultSet => Any)(implicit conn: Connection, ec: ExecutionContext): Future[Any] = Future {
    val resultStatement = withCloseable(conn.createStatement) { statement =>
      maybeCurrentStatement.set(Some(statement))
      val rs = statement.executeQuery(statementString)
      val resultBody: Try[Any] = withCloseable(rs) { resultSet =>
        body(resultSet)
      }
      maybeCurrentStatement.set(None)
      resultBody
    }
    resultStatement.flatten
  }
}
