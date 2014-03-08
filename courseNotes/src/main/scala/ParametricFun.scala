object ParametricFun {
  def hasValue[T] (option: Option[T]): Boolean = option.isDefined

  println(s"hasValue[Int](Some(1)) = ${hasValue[Int](Some(1))}")
  println(s"hasValue[Int](None) = ${hasValue[Int](None)}")
  println(s"hasValue(None) = ${hasValue(None)}")
  println(s"""hasValue[String](Some("a")) = ${hasValue[String](Some("a"))}""")
  println(s"""hasValue[(Int,Int)](Option((1,2))) = ${hasValue[(Int,Int)](Option((1,2)))}""")
  println(s"""hasValue[Array[Int]](Some(Array(1,2))) = ${hasValue[Array[Int]](Some(Array(1,2)))}""")

  println(s"hasValue[(Some(1)) = ${hasValue(Some(1))}")
  println(s"""hasValue(Some("a")) = ${hasValue(Some("a"))}""")
  println(s"""hasValue(Option((1,2))) = ${hasValue(Option((1,2)))}""")
  println(s"""hasValue(Some(Array(1,2))) = ${hasValue(Some(Array(1,2)))}""")
}

object TimeableT extends App {
  class Timeable[T] {
    def time(block: => T): T = {
      val t0 = System.nanoTime()
      val result: T = block
      val elapsedMs = (System.nanoTime() - t0) / 1000000
      println("Elapsed time: " + elapsedMs + "ms")
      result
    }
  }

  def calculatePiFor(decimalPlaces: Int): Double = {
    var acc = 0.0
    for (i <- 0 until decimalPlaces)
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  println("Pi=" + new Timeable[Double].time { calculatePiFor(100000) })
  println("page=" + new Timeable[String].time { io.Source.fromURL("http://scalacourses.com").mkString.trim })
}
