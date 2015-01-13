package collections {
  class Thing(val i: Int, val s: String) extends Ordered[Thing] {
    def compare(that: Thing) = {
      val primaryKey = this.i - that.i
      if (primaryKey != 0) primaryKey else this.s compare that.s
    }

    override def equals(other: Any) = {
      val that = other.asInstanceOf[Thing]
      this.i == that.i && this.s == that.s
    }

    override def hashCode = super.hashCode

    override def toString = s"Thing($i, $s)"
  }
}

package object collections {
  val thing1 = new Thing(1, "x")
  val thing2 = new Thing(1, "y")
  val thing3 = new Thing(33, "b")
  val thing4 = new Thing(4, "m")
  val thing5 = new Thing(4, "n")
  val things = Array(thing1, thing2, thing3, thing4, thing5)
}
