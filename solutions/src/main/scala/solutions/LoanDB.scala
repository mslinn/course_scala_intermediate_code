package solutions

import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Connection
import scala.collection.immutable.Map
import scala.util.{Failure, Success, Try}

trait Closeable {
  //type Closer = AutoCloseable // this works fine, but there is a more flexible approach:
  /** Alias for structural type that has a method called close which does not accept parameters and returns nothing */
  type Closer = { def close(): Unit }

  /** Modified to handle Closeable or AutoCloseable by using a structural type */
  def withCloseable[C <: Closer, T](factory: => C)(operation: C => T): Try[T] = {
    import scala.language.reflectiveCalls
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

/** Mix in AdvancedDBOps instead of DBOps to alternate the implementation */
object LoanDB extends App with Closeable with DBOps {
  withCloseable(connectTable("jdbc:sqlite:person.db")) { implicit conn: Connection =>
    insert("person", Map("id" -> 1, "name" -> "Fred Flintsone",  "age" -> 400002))
    insert("person", Map("id" -> 2, "name" -> "Wilma Flintsone", "age" -> 400001))
    insert("person", Map("id" -> 3, "name" -> "Barney Rubble",   "age" -> 400004))
    insert("person", Map("id" -> 4, "name" -> "Betty Rubble",    "age" -> 400003))

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
