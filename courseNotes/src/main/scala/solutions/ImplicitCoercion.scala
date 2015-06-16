package solutions

object ImplicitCoercion extends App {
  case class Complex(re: Double, im: Double) {
    def + (that: Complex) = new Complex(re + that.re, im + that.im)

    def -(that: Complex) =  new Complex(this.re - that.re, this.im - that.im)

    override def toString = s"$re + ${im}i"
  }

  implicit def doubleToComplex(d: Double) = Complex(d, 0)

  implicit def tupleToComplex1(t: (Int, Int))       = Complex(t._1, t._2)
  implicit def tupleToComplex2(t: (Int, Float))     = Complex(t._1, t._2)
  implicit def tupleToComplex3(t: (Float, Int))     = Complex(t._1, t._2)
  implicit def tupleToComplex4(t: (Float, Float))   = Complex(t._1, t._2)
  implicit def tupleToComplex5(t: (Int, Double))    = Complex(t._1, t._2)
  implicit def tupleToComplex6(t: (Double, Int))    = Complex(t._1, t._2)
  implicit def tupleToComplex7(t: (Double, Double)) = Complex(t._1, t._2)
  implicit def tupleToComplex8(t: (Float, Double))  = Complex(t._1, t._2)
  implicit def tupleToComplex9(t: (Double, Float))  = Complex(t._1, t._2)

  println(s"""Complex(2, 5) + 5.0 = ${Complex(2, 5) + 5.0}""")
  println(s"""5.0 + Complex(1, -2) = ${5.0 + Complex(1, -2)}""")

  println(s"""Complex(2, 5) + (2f, 5) = ${Complex(2, 5) + (2f, 5)}""")
  println(s"""Complex(2, 5) + (2, 5f) = ${Complex(2, 5) + (2, 5f)}""")
  println(s"""Complex(2, 5) + (2f, 5f) = ${Complex(2, 5) + (2f, 5f)}""")

  println(s"""Complex(2, 5) + (2d, 5) = ${Complex(2, 5) + (2d, 5)}""")
  println(s"""Complex(2, 5) + (2, 5d) = ${Complex(2, 5) + (2, 5d)}""")
  println(s"""Complex(2, 5) + (2d, 5d) = ${Complex(2, 5) + (2d, 5d)}""")

  println(s"""Complex(2, 5) + (2f, 5d) = ${Complex(2, 5) + (2f, 5d)}""")
  println(s"""Complex(2, 5) + (2d, 5f) = ${Complex(2, 5) + (2d, 5f)}""")
  println(s"""Complex(2, 5) + (2d, 5d) = ${Complex(2, 5) + (2d, 5d)}""")
}
