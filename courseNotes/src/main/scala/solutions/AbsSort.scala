package solutions

object AbsSort extends App {
  val result = List(3, -7, 5, -2).sortWith(math.abs(_)<math.abs(_))
  println(s"List(3, -7, 5, -2).sortWith(math.abs(_)<math.abs(_)) = $result")
}
