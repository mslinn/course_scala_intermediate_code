package solutions

object FutureDB extends App with DBOps {
  import java.sql.Connection
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.Duration
  import scala.concurrent.{Await, Future}

  withCloseable(connectTable("jdbc:sqlite:person.db")) { implicit conn: Connection =>
    insert("person", Map("id" -> 1, "name" -> "Fred Flintsone",  "age" -> 400002))
    insert("person", Map("id" -> 2, "name" -> "Wilma Flintsone", "age" -> 400001))
    insert("person", Map("id" -> 3, "name" -> "Barney Rubble",   "age" -> 400004))
    insert("person", Map("id" -> 4, "name" -> "Betty Rubble",    "age" -> 400003))

    Await.result(Future(execute("select * from person order by id") { resultSet =>
      Future(cancel()) // comment this line to see entire result set
      val columnCount: Int = resultSet.getMetaData.getColumnCount
      do {
        val x = (1 to columnCount).map(resultSet.getObject).mkString(", ")
        println(x)
      } while (resultSet.next)
    }), Duration.Inf)
  }
}
