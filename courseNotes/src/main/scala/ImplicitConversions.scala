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

object ImplicitDefaultValues extends App {
  def asdf(implicit x: Int=3): Unit = println(x)

  implicit val y=2

  println(s"""asdf()=${asdf()}""")
}

object ImplicitCoercion extends App {
  case class Complex(re: Double, im: Double) {
    def + (another: Complex) =
      new Complex(re + another.re, im + another.im)
  }

  implicit def doubleToComplex(d: Double) = Complex(d, 0)

  println(s"""Complex(2.0, 5.0) + 5.0 = ${Complex(2.0, 5.0) + 5.0}""")
  println(s"""5.0 + Complex(1.0, -2.0) = ${5.0 + Complex(1.0, -2.0)}""")
}

object ImplicitlyConversion extends App {
  val i2l = implicitly[Function[Int, Long]]
  println(s"""i2l(3)=${i2l(3)}""")

  println(s"""implicitly[Int => Long].apply(5)=${implicitly[Int => Long].apply(5)}""")
}