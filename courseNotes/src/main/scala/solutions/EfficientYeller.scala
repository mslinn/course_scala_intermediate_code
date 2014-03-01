package solutions.yeller

object EfficientYeller extends App {
  println("Look out".yell)
}

/** This value class does not require new object allocations */
case class Yeller(s: String) extends AnyVal {
  def yell: String = s.toUpperCase + "!!"
}

object `package` {
  implicit def stringToYeller(s: String): Yeller = Yeller(s)
}
