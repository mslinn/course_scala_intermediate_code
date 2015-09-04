import scala.language.implicitConversions

object ImplicitConversions extends App {
  implicit val defaultMultiplier = Multiplier3(2)

  implicit val defaultDivider = Divider3(3)

  def multiply(value: Int)(implicit multiplier: Multiplier3): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider3): Int = value / divider.value

  implicit def intToMultiplier(int: Int): Multiplier3 = Multiplier3(int)

  implicit def intToDivider(int: Int): Divider3 = Divider3(int)

  println(s"multiply(2)(3)=${multiply(2)(3)}")
  println(s"multiply(5)=${multiply(5)}")
  println(s"divide(12)(4)=${divide(12)(4)}")
  println(s"divide(9)=${divide(9)}")
}

package yeller {
  case class Yeller(s: String) {
    def yell: String = s.toUpperCase + "!!"
  }

  object `package` {
    implicit def stringToYeller(s: String): Yeller = Yeller(s)
  }
}

object YellerMain extends App {
  import yeller._

  println("Look out".yell)
}

object ImplicitDefaultValues extends App {
  def asdf(implicit x: Int=3): Unit = println(x)

  implicit val y = 2

  println(s"""asdf=$asdf""")
}

object InnerScope {
  implicit val list2 = List(1, 2, 3)

  def res(implicit list: List[Int]): List[Int] = list
}

object OuterScope extends App {
  implicit val list = List(1, 2)

  println(InnerScope.res)
}

object ImportedImplicit extends App {
  import InnerScope._

  println(res)
}

object CompanionScope extends App {
  class A(val n: Int) {
    def +(other: A) = new A(n + other.n)
  }

  object A {
    implicit def fromInt(n: Int): A = new A(n)
  }

  val x = 1 + new A(1) // is converted into:
  val y = A.fromInt(1) + new A(1)

  println(s"x.n=${x.n}")
  println(s"y.n=${y.n}")
}

object ImplicitCoercion extends App {
  case class Complex(re: Double, im: Double) {
    def +(that: Complex): Complex = Complex(re + that.re, im + that.im)

    def -(that: Complex): Complex = Complex(this.re - that.re, this.im - that.im)

    override def toString = s"$re + ${im}i"
  }

  implicit def doubleToComplex(d: Double): Complex = Complex(d, 0)

  println(s"""Complex(2.0, 5.0) + 5.0 = ${Complex(2.0, 5.0) + 5.0}""")
  println(s"""Complex(2.0, 5.0) + 6 = ${Complex(2.0, 5.0) + 6}""")
  println(s"""Complex(2.0, 5) + 6f = ${Complex(2.0, 5.0) + 6f}""")
  println(s"""5.0 + Complex(1.0, -2.0) = ${5.0 + Complex(1.0, -2.0)}""")
}

object ImplicitlyConversion extends App {
  val i2l = implicitly[Function[Int, Long]]
  println(s"""i2l(3)=${i2l(3)}""")

  println(s"""implicitly[Int => Long].apply(5)=${implicitly[Int => Long].apply(5)}""")
}
