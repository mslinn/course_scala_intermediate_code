import annotation.implicitNotFound

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

object With2 extends App {
  case class Blarg(i: Int, s: String)

  def withBlarg(blarg: Blarg)(operation: Blarg => Unit):  Unit = operation(blarg)

  def double(implicit blarg: Blarg): Blarg = blarg.copy(i=blarg.i*2, s=blarg.s*2)

  def triple(implicit blarg: Blarg): Blarg = blarg.copy(i=blarg.i*3, s=blarg.s*3)

   withBlarg(Blarg(1, "asdf ")) { blarg =>
    println(double(blarg))
    println(triple(blarg))
  }

  withBlarg(Blarg(1, "qwer ")) { implicit blarg =>
    println(double)
    println(triple)
  }
}

@implicitNotFound("Cannot find implicit of type Multiplier2 in scope")
case class Multiplier2(value: Int) extends AnyVal

@implicitNotFound("Cannot find implicit of type Divider2 in scope")
case class Divider2(value: Int) extends AnyVal

object ImplicitOps2 extends App {
  implicit val defaultMultiplier = Multiplier2(2)

  implicit val defaultDivider = Divider2(3)

  def multiply(value: Int)(implicit multiplier: Multiplier2): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider2): Int = value / divider.value

  println(s"multiply(2)(3)=${multiply(2)(Multiplier2(3))}")
  println(s"multiply(5)=${multiply(5)}")
  println(s"divide(12)(4)=${divide(12)(Divider2(4))}")
  println(s"divide(9)=${divide(9)}")
}

object ImplicitOps3 extends App {
  implicit val defaultMultiplier = Multiplier2(2)

  implicit val defaultDivider = Divider2(3)

  def multiply(value: Int)(implicit multiplier: Multiplier2): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider2): Int = value / divider.value

  implicit def intToMultiplier(int: Int): Multiplier2 = Multiplier2(int)

  implicit def intToDivider(int: Int): Divider2 = Divider2(int)

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

package efficientYeller {
  /** This value class does not require new object allocations */
  case class Yeller(val s: String) extends AnyVal {
    def yell: String = s.toUpperCase + "!!"
  }

  object `package` {
    implicit def stringToYeller(s: String): Yeller = Yeller(s)
  }
}

object EfficientYellerMain extends App {
  import efficientYeller._

  println("Look out".yell)
}
