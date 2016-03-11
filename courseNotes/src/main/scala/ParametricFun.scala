object ParametricFun extends App {
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

object ScopedParams extends App {
  case class Outer(propOuter: String)
  case class Inner(propInner: String)

  class Test[T] {
    def print[U](u: U, t: T): Unit = println(s"Test.test: u=$u; t=$t")
  }

  new Test[Outer].print(Inner("inner"), Outer("outer"))


  class Shadow[T] {
    def print[T](t: T): Unit = println(s"Test.test: y=$t")
  }

  new Shadow[Outer].print(Inner("inner"))
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
  def withT[T, U](t: T)(operation: T => U): U = operation(t)

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
  import scala.language.implicitConversions

  class TernaryThen(predicate: => Boolean) {
    def ?[A](thenClause: => A) = new TernaryEval(thenClause)

    class TernaryEval[A](thenClause: => A) {
      def |(elseClause: => A): A = if (predicate) thenClause else elseClause
    }
  }

  implicit def toTernaryThen(predicate: => Boolean): TernaryThen = new TernaryThen(predicate)

  println(s"""util.Random.nextBoolean ? "Yes" | "No" = ${ util.Random.nextBoolean ? "Yes" | "No" }""")

  val x: TernaryThen#TernaryEval[String] = util.Random.nextBoolean ? "Yup"
  println(s"""util.Random.nextBoolean ? "Yup" | "Nope" = ${ x | "Nope" }""")
}

object Ternary2 extends App {
  implicit def boolToOperator(predicate: Boolean) = new {
    def ?[A](trueClause: => A) = new {
      def |(falseClause: => A) = if (predicate) trueClause else falseClause
    }
  }

  println(s"""util.Random.nextBoolean ? "Yes" | "No" = ${ util.Random.nextBoolean ? "Yes" | "No" }""")
  val result = util.Random.nextBoolean.?("Yes").|("No")
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

object ExtendJavaSet extends App {
  trait IgnoredCaseSet[T] extends java.util.Set[T] {
    abstract override def add(t: T): Boolean =
      t match {
        case string: String => super.add(string.toLowerCase.asInstanceOf[T])

        case obj => super.add(obj)
      }

    abstract override def contains(obj: Object) =
      obj match {
        case s: String =>
          super.contains(s.toLowerCase)

        case o =>
          super.contains(o)
      }
  }

  class MySet extends java.util.HashSet[String] with IgnoredCaseSet[String]

  val mySet = new MySet() // Java sets are mutable, only the reference is immutable
  mySet.add("One")
  mySet.add("Two")
  mySet.add("Three")
  println(s"mySet=$mySet")
}

