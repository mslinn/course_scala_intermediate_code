import annotation.implicitNotFound

object ImplicitValues extends App {
  implicit val defaultMultiplier: Int = 2

  def multiply(value: Int)(implicit multiplier: Int): Int = value * multiplier

  println(s"""multiply(2)(3)=${multiply(2)(3)}""")
  println(s"""multiply(5)=${multiply(5)}""")
}

case class Multiplier(value: Int)

case class Divider(value: Int)

object ImplicitValues2 extends App {
  implicit val defaultMultiplier: Multiplier = Multiplier(2)

  implicit val defaultDivider: Divider = Divider(3)

  def multiply(value: Int)(implicit multiplier: Multiplier): Int = value * multiplier.value

  def divide(value: Int)(implicit divider: Divider): Int = value / divider.value

  println(s"multiply(2)(Multiplier(3))=${ multiply(2)(Multiplier(3)) }")
  println(s"multiply(5)=${ multiply(5) }")
  println(s"divide(12)(Divider(4))=${ divide(12)(Divider(4)) }")
  println(s"divide(9)=${ divide(9) }")
}

@implicitNotFound("Cannot find implicit of type Multiplier3 in scope")
case class Multiplier3(value: Int) extends AnyVal

@implicitNotFound("Cannot find implicit of type Divider3 in scope")
case class Divider3(value: Int) extends AnyVal

object ImplicitValues3 extends App {
  implicit val defaultMultiplier: Multiplier3 = Multiplier3(2)

  implicit val defaultDivider: Divider3 = Divider3(3)

  def multiply(value: Int)
              (implicit multiplier: Multiplier3): Int = value * multiplier.value

  def divide(value: Int)
            (implicit divider: Divider3): Int = value / divider.value

  println(s"multiply(2)(3)=${multiply(2)(Multiplier3(3))}")
  println(s"multiply(5)=${multiply(5)}")
  println(s"divide(12)(4)=${divide(12)(Divider3(4))}")
  println(s"divide(9)=${divide(9)}")
}

object ImplicitValues4 extends App {
  @implicitNotFound("Cannot find implicit of type Multiplier4 in scope")
  class Multiplier4(val value: Int) extends AnyVal

  @implicitNotFound("Cannot find implicit of type Divider4 in scope")
  class Divider4(val value: Int) extends AnyVal

  implicit val defaultMultiplier: Multiplier4 = new Multiplier4(2)

  implicit val defaultDivider: Divider4 = new Divider4(3)

  def multiply(value: Int)
              (implicit multiplier: Multiplier4): Int = value * multiplier.value

  def divide(value: Int)
            (implicit divider: Divider4): Int = value / divider.value

  println(s"multiply(2)(3)=${ multiply(2)(new Multiplier4(3)) }")
  println(s"multiply(5)=${ multiply(5) }")
  println(s"divide(12)(4)=${ divide(12)(new Divider4(4)) }")
  println(s"divide(9)=${ divide(9) }")
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

object SLS7_1 extends App {
  case class Blarg(i: Int, s: String) {
    override def toString = s"$i $s"
  }

  class Sls71(a: Int, private implicit val blarg: Blarg) {
    def double(implicit blarg: Blarg): Blarg = blarg.copy(i=blarg.i*2, s=blarg.s*2)
    def triple(implicit blarg: Blarg): Blarg = blarg.copy(i=blarg.i*3, s=blarg.s*3)

    val bigBlarg: Blarg = if (a<10) double else triple
  }

  print("> ")
  Iterator.continually(io.StdIn.readLine())
          .takeWhile(_ != null)
          .foreach { line =>
    try {
      val i = line.toInt
      val blarg = Blarg(i, "nom ")
      val sls71 = new Sls71(i, blarg)
      println(sls71.bigBlarg.s)
      print("> ")
    } catch {
      case _: Exception => sys.exit
    }
  }
}
