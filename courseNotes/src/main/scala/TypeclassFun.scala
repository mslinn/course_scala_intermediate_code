object TypeclassFun extends App {

  trait Quantified[A] { def quantify(a: A): Int }

  implicit val stringQuantifiable = new Quantified[String] { def quantify(s: String): Int = s.length }

  implicit val intQuantifiable = new Quantified[Int] { def quantify(int: Int): Int = int }

  def sumQuantities[A : Quantified](as: List[A]) = as.map(implicitly[Quantified[A]].quantify).sum
  // Same as:
  //def sumQuantities[A](as: List[A])(implicit evidence: Quantified[A]) = as.map(evidence.quantify).sum

  val stringQuantities = sumQuantities(List("abc", "defghi"))
  println(s"""sumQuantities(List("abc", "defghi")) = $stringQuantities""")

  val intQuantities = sumQuantities(List(1, 2, 3))
  println(s"""sumQuantities(List(1, 2, 3)) = $intQuantities""")
}
