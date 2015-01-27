package solutions

object TypeAhead extends App with DBOps {
  import java.sql.Connection

  def createDB(implicit connection: Connection): Unit = {
    insert("person", Map("id" -> 1, "name" -> "Fred Flintstone",  "age" -> 400002))
    insert("person", Map("id" -> 2, "name" -> "Wilma Flintstone", "age" -> 400001))
    insert("person", Map("id" -> 3, "name" -> "Barney Rubble",   "age" -> 400004))
    insert("person", Map("id" -> 4, "name" -> "Betty Rubble",    "age" -> 400003))
  }

  def dumpDB(implicit connection: Connection): Unit =
    execute("select * from person order by id") { resultSet =>
      val columnCount: Int = resultSet.getMetaData.getColumnCount
      do {
        val x = (1 to columnCount).map(resultSet.getObject).mkString(", ")
        println(x)
      } while (resultSet.next)
    }

  def getConnection: Connection = connectTable("jdbc:sqlite:person.db")

  withCloseable(getConnection) { implicit conn =>
    createDB
    dumpDB
  }
}
