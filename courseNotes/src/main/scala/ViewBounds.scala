class X(val x: Int)

class Y(val x: Int, val y: Int)

class Z(val x: Int, val y: Int, val z: Int) {
  def sum = x + y + z
}

object ViewBounds {
  implicit def intToX(i: Int): X = new X(i)
  implicit def xToY[XView <% X](x: XView): Y = new Y(x.x, 0)
  implicit def yToZ[YView <% Y](y: YView): Z = new Z(y.x, y.y, 0)

  println(5.sum)
  println(new Y(2, 3).sum)
  println(new Z(1, 2, 3).sum)

  implicitly[Ordering[(Int, String)]].compare( (1, "b"), (1, "a") )
}


object TypesafeEquality extends App {
  class Dog3(val name: String) {
    override def equals(that: Any): Boolean = canEqual(that) && hashCode==that.hashCode

    override def hashCode = name.hashCode

    def canEqual(that: Any) : Boolean = that.isInstanceOf[Dog3]

    def ===[D <% Dog3](that: D): Boolean = this==that
  }

  val dog3 = new Dog3("Fido3")
  val maybeDog3 = Some(dog3)
  //println(s"Comparing dog3 with maybeDog3: ${dog3===maybeDog3}")
}