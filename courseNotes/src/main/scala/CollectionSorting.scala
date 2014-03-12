object CollectionSorting extends App {
  println(s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${List(3, 7, 5, 2).sortWith((x, y) => x < y)}""")
  println(s"""List(3, 7, 5, 2).sortWith(_ < _) = ${List(3, 7, 5, 2).sortWith(_ < _)}""")

  class Thing(val i: Int, val s: String) extends Ordered[Thing] {
    def compare(that: Thing) =  {
      val primaryKey = this.i - that.i
      if (primaryKey!=0) primaryKey else this.s compare that.s
    }

    override def equals(other: Any) = {
      val that = other.asInstanceOf[Thing]
      this.i == that.i && this.s==that.s
    }

    override def hashCode = super.hashCode

    override def toString = s"Thing($i, $s)"
  }

  val thing1 = new Thing(1, "z")
  val thing2 = new Thing(2, "y")
  val thing3 = new Thing(3, "x")

  println(s"""thing1>thing2 = ${thing1>thing2}""")
  println(s"""thing1<thing2 = ${thing1<thing2}""")
  println(s"""thing1 == Thing(1, "z") = ${thing1 == new Thing(1, "z")}""")
  println(s"""thing1<=thing2 = ${thing1<=thing2}""")
  println(s"""thing1>=thing2 = ${thing1>=thing2}""")

  val things = Array(thing2, thing1, thing3)
  println(s"""things.sorted = ${things.sorted}""")

  class Thang(val i: Int, val s: String) extends Ordering[Thang] {
    def compare(a: Thang, b: Thang) =  {
      val primaryKey = a.i - b.i
      if (primaryKey!=0) primaryKey else a.s compare b.s
    }

    override def equals(other: Any) = {
      val that = other.asInstanceOf[Thang]
      this.i == that.i && this.s==that.s
    }

    override def hashCode = super.hashCode

    override def toString = s"Thang($i, $s)"
  }

  object Thang {
    implicit val ThangOrdering = Ordering.by { thang: Thang =>
      (thang.i, thang.s)
    }
  }

  val orderByI = Ordering.by { thang: Thang => thang.i }
  val orderByS = Ordering.by { thang: Thang => thang.s }
  val thangs = Array(new Thang(1, "x"), new Thang (33, "b"), new Thang(4, "m"))

  println(s"""thangs.sorted(orderByI) = ${thangs.sorted(orderByI)}""")
  println(s"""thangs.sorted(orderByS) = ${thangs.sorted(orderByS)}""")

  val ordering = implicitly[Ordering[(Int, String)]]
  println(s"""ordering.compare( (1, "b"), (1, "a") ) = ${ordering.compare( (1, "b"), (1, "a") )}""")
  println(s"""ordering.compare( (1, "b"), (1, "b") ) = ${ordering.compare( (1, "b"), (1, "b") )}""")
  println(s"""ordering.compare( (1, "b"), (1, "c") ) = ${ordering.compare( (1, "b"), (1, "c") )}""")
}
