import scala.collection.compat._

object Hello extends App {
  val array = Array(1, 2, 3)
  println("Scala 2.13 syntax; Array(1, 2, 3).to(List): " + array.to(List))
  println("Older Scala syntax; Array(1, 2, 3).to[List]: " + array.to[List])
}
