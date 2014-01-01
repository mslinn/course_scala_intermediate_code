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

  val dog3a = new Dog3("Fido3a")
  val dog3b = new Dog3("Fido3b")
  val maybeDog3a = Some(dog3a)
  println(s"Comparing dog3 with itself: ${dog3a===dog3b}")
  //\println(s"Comparing dog3 with maybeDog3: ${dog3a===maybeDog3a}")
}

object AllEquality extends App {
  implicit class AnyEquality[T](thiz: T) {
    def ===[U <% T](that: U): Boolean = thiz==that
  }

  println(s"Comparing 3 with 4: ${3===4}")
  //println(s"Comparing 3 with Some(3): ${3===Some(3)}")
}
