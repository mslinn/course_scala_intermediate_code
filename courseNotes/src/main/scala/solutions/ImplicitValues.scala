package solutions

import scala.annotation.implicitNotFound

object ImplicitValues extends App {
  @implicitNotFound("Cannot find implicit of type Multiplier4 in scope")
  class Multiplier4(val value: Int) extends AnyVal

  @implicitNotFound("Cannot find implicit of type Divider4 in scope")
  class Divider4(val value: Int) extends AnyVal

  @implicitNotFound("Cannot find implicit of type Squarer in scope")
  class Squarer(val value: Int) extends AnyVal

  implicit val defaultMultiplier = new Multiplier4(2)

  implicit val defaultDivider = new Divider4(3)

  implicit val defaultSquarer = new Squarer(4)

  def multiply(value: Int)(implicit multiplier: Multiplier4): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider4): Int = value / divider.value

  def square(implicit squarer: Squarer): Int = squarer.value * squarer.value

  println(s"multiply(2)(3)=${multiply(2)(new Multiplier4(3))}")
  println(s"multiply(5)=${multiply(5)}")
  println(s"divide(12)(4)=${divide(12)(new Divider4(4))}")
  println(s"divide(9)=${divide(9)}")
  println(s"square(5)=${square(new Squarer(5))}")
  println(s"square=${square}")
}
