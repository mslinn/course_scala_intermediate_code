case class Multiplier(value: Int)

case class Divider(value: Int)

object ImplicitOps extends App {
  implicit val defaultMultiplier = Multiplier(2)

  implicit val defaultDivider = Divider(3)

  def multiply(value: Int)(implicit multiplier: Multiplier): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider): Int = value / divider.value

  println(s"multiply(2)(Multiplier(3))=${multiply(2)(Multiplier(3))}")
  println(s"multiply(5)=${multiply(5)}")
  println(s"divide(12)(Divider(4))=${divide(12)(Divider(4))}")
  println(s"divide(9)=${divide(9)}")
}

object ImplicitOps2 extends App {
  implicit val defaultMultiplier = Multiplier(2)

  implicit val defaultDivider = Divider(3)

  def multiply(value: Int)(implicit multiplier: Multiplier): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider): Int = value / divider.value

  implicit def intToMultiplier(int: Int): Multiplier = Multiplier(int)

  implicit def intToDivider(int: Int): Divider = Divider(int)

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

object AppleFanBoi extends App {
  implicit class IosInt(i: Int) { def s: Int = i + 1 }

  println(s"I have an iPhone ${4.s}")
}

/** @see https://issues.scala-lang.org/browse/SI-4975?jql=text%20~%20%22implicit%20class%22 */
object ImplicitOps3 extends App {
  implicit val defaultMultiplier = Multiplier(2)

  implicit val defaultDivider = Divider(3)

  def multiply(value: Int)(implicit multiplier: Multiplier): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider): Int = value / divider.value

  //println(s"multiply(2)(3)=${multiply(2)(3)}")
  println(s"multiply(5)=${multiply(5)}")
  //println(s"divide(12)(4)=${divide(12)(4)}")
  println(s"divide(9)=${divide(9)}")
}
