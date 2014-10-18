package solutions

object PartialFunSoln extends App {
  val squareRoot: PartialFunction[Double, Double] = {
     case d if d >= 0 => math.sqrt(d)
  }

  println(s"squareRoot.isDefinedAt(-1)=${squareRoot.isDefinedAt(-1)}")
  println(s"squareRoot(3)=${squareRoot(3)}")

  val sqrtList1 = List(0.5, -0.2, 4).collect(squareRoot)
  val sqrtList2 = List(0.5, -0.2, 4) collect squareRoot
}
