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

object ParametricWith extends App {
  def withT[T](t: T)(operation: T => Unit):  Unit = { operation(t) }

  withT(6) { i => println( i * 2) }
  withT(new java.util.Date) { println }
  withT(Blarg(1, "hi")) { blarg => println(blarg) }

  case class Blarg(i: Int, s: String)
}

package parametricSimulation {
  abstract class AbstractSimulation[X, Y, Z] {
    def simulate(x: X, y: Y, z: Z): String
  }

  object AbstractSimulation {
    implicit lazy val defaultSimulation = new AbstractSimulation[Int, Double, String] {
      def simulate(x: Int, y: Double, z: String) = s"Companion simulation: $x, $y, $z"
    }
  }

  trait Implicit {
    implicit lazy val defaultSimulation = new AbstractSimulation[Int, Double, String] {
      def simulate(x: Int, y: Double, z: String) = s"Implicit simulation: $x, $y, $z"
    }
  }

  object ParametricSimulation extends App {
    object CompanionSimulation {
      println(implicitly[AbstractSimulation[Int, Double, String]].simulate(1, 2, "three"))
    }

    object TraitSimulation extends Implicit {
      println(implicitly[AbstractSimulation[Int, Double, String]].simulate(10, 20, "thirty"))
    }

    CompanionSimulation
    TraitSimulation
  }
}

object Ternary1 extends App {
  class TernaryThen(predicate: => Boolean) {
    def ?[A](thenClause: => A) = new TernaryEval(thenClause)

    class TernaryEval[A](thenClause: => A) {
      def |(elseClause: => A): A = if (predicate) thenClause else elseClause
    }
  }

  implicit def toTernaryThen(predicate: => Boolean) = new TernaryThen(predicate)

  println(s"""(4*4 > 14) ? "Yes" | "No" = ${(4*4 > 14) ? "Yes" | "No"}""")

  val x = (4*4 > 14) ? "Yup"
  println(s"""(4*4 > 14) ? "Yup" | "Nope" = ${x | "Nope"}""")
}

object Ternary2 extends App {
  implicit def boolToOperator(predicate: Boolean) = new {
    def ?[A](trueClause: => A) = new {
      def |(falseClause: => A) = if (predicate) trueClause else falseClause
    }
  }

  println(s"""(4*4 > 14) ? "Yes" | "No" = ${(4*4 > 14) ? "Yes" | "No"}""")
  val result = (4*4 > 14).?("Yes").|("No")
}

object RichInterface extends App {
  trait RichIterable[A] {
    def iterator: java.util.Iterator[A]

    def foreach(f: A => Unit): Unit = {
      val iter = iterator
      while (iter.hasNext) f(iter.next)
    }

    def foldLeft(seed: A)(f: (A, A) => A): A = {
      var result: A = seed
      foreach { item =>
        result = f(result, item)
      }
      result
    }
  }

  val richSet = new java.util.HashSet[Int] with RichIterable[Int]
  richSet.add(1)
  richSet.add(2)
  richSet.add(6)
  richSet.add(13)
  val total = richSet.foldLeft(0)(_ + _)
  println(s"richSet = ${richSet.toArray.mkString(", ")}; total = $total")
}