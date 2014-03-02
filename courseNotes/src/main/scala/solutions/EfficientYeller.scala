package solutions.yeller

case class Yeller(s: String) extends AnyVal {
  def yell: String = s.toUpperCase + "!!"
  def whisper: String = "Shhh! " + s.toLowerCase
}

object `package` {
  implicit def stringToYeller(s: String): Yeller = Yeller(s)
}

object EfficientYeller extends App {
  println("Look out".yell)
  println("This is a secret".whisper)
}
