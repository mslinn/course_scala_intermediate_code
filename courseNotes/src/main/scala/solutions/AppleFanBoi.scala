package solutions

object AppleFanBoi extends App {
  implicit class IosInt(val i: Int) extends AnyVal { def s: Int = i + 1 }

  println(s"I have an iPhone ${4.s}")
}
