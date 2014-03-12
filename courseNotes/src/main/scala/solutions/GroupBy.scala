package solutions

object GroupBy extends App {
  val map = Map( "selectedKeys"->Seq("one", "two", "three"), "otherKeys"->Seq("two", "one") )
  val inversion = map.groupBy(_._1)

  val answer1 = inversion.map { x =>
    val value = x._2(x._1)
    x._1 -> value
  }

  val answer2 = inversion.values.flatten.toMap

  println(s"answer1 = $answer1")
  println(s"answer2 = $answer2")
}
