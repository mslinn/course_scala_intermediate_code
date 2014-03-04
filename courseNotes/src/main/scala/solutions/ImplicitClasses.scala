package solutions

object ImplicitClasses extends App {
  import solutions.ImplicitCoercion.Complex

  implicit class RichComplex(val complex: Complex) extends AnyVal {
    def abs: Double = math.sqrt(complex.re * complex.re + complex.im * complex.im)
  }

  println(s"Complex(3, 4).abs=${Complex(3, 4).abs}")
}
