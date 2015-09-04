package solutions

object FutureDB extends App with DBOps {
  import java.sql.Connection
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.Duration
  import scala.concurrent.{Await, Future}

  case class Person(name: String, age: Int, id: Option[Long]=None)

  connectTable("jdbc:sqlite:person.db").foreach { table =>
    withCloseable(table) { implicit conn: Connection =>
      val futures: List[Future[Int]] = List(
        Person("Fred Flintstone", 400002),
        Person("Wilma Flintstone", 400001),
        Person("Barney Rubble", 400004),
        Person("Betty Rubble", 400003)
      ).zipWithIndex.map { case (person, i) =>
        insert("person", Map("name" -> person.name, "age" -> person.age, "id" -> i))
      }

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
}
