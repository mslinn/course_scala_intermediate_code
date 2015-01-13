package collections

object CollectionOrdered extends App {
  println( s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${List(3, 7, 5, 2).sortWith((x, y) => x < y)}""")
  println( s"""List(3, 7, 5, 2).sortWith(_ < _) = ${List(3, 7, 5, 2).sortWith(_ < _)}""")

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

  val thing1 = new Thing(1, "z")
  val thing2 = new Thing(2, "y")
  val thing3 = new Thing(3, "x")

  println( s"""thing1>thing2 = ${thing1 > thing2}""")
  println( s"""thing1<thing2 = ${thing1 < thing2}""")
  println( s"""thing1 == Thing(1, "z") = ${thing1 == new Thing(1, "z")}""")
  println( s"""thing1<=thing2 = ${thing1 <= thing2}""")
  println( s"""thing1>=thing2 = ${thing1 >= thing2}""")

  val things = Array(thing2, thing1, thing3)
  println( s"""things.sorted = ${things.sorted.mkString(", ")}""")
  println( s"""things before quicksort = ${things.mkString(", ")}""")
  util.Sorting.quickSort(things) // modifies things Array
  println( s"""things after quicksort = ${things.mkString(", ")}""")
  println( s"""things.sortWith((x, y) => x.i > y.i)=${things.sortWith((x, y) => x.i > y.i).mkString(", ")}""")
  println( s"""things.sortWith((x, y) => x.s > y.s)=${things.sortWith((x, y) => x.s > y.s).mkString(", ")}""")
}

object CollectionOrdering extends App {
  class Thang(val i: Int, val s: String) extends Ordered[Thang] {
    def compare(that: Thang): Int =  {
      val primaryKey = this.i - that.i
      if (primaryKey!=0) primaryKey else this.s compare that.s
    }

    override def equals(other: Any): Boolean = {
      val that = other.asInstanceOf[Thang]
      this.i == that.i && this.s==that.s
    }

    override def hashCode: Int = super.hashCode

    override def toString = s"Thang($i, $s)"
  }

  val thangs = Array(new Thang(1, "x"), new Thang(1, "y"), new Thang (33, "b"), new Thang(4, "m"), new Thang(4, "n"))

  println(s"""thangs = ${thangs.mkString(", ")}""")
  println(s"""thangs.sorted = ${thangs.sorted.mkString(", ")}""")
  println(s"""thangs.sorted(Ordering[Thang]) = ${thangs.sorted(Ordering[Thang]).mkString(", ")}""")

  val orderByI: Ordering[Thang] = Ordering.by { _.i }
  println(s"""thangs.sorted(orderByI) = ${thangs.sorted(orderByI).mkString(", ")}""")

  val orderByIReverse: Ordering[Thang] = orderByI.reverse
  println(s"""thangs.sorted(orderByIReverse) = ${thangs.sorted(orderByIReverse).mkString(", ")}""")

  val orderByS: Ordering[Thang] = Ordering.by { _.s }
  println(s"""thangs.sorted(orderByS) = ${thangs.sorted(orderByS).mkString(", ")}""")

  val orderBySReverse: Ordering[Thang] = orderByS.reverse
  println(s"""thangs.sorted(orderBySReverse) = ${thangs.sorted(orderBySReverse).mkString(", ")}""")

  val orderBySandI: Ordering[Thang] = Ordering.by { x => (x.s, x.i) }
  println(s"""thangs.sorted(orderBySandI) = ${thangs.sorted(orderBySandI).mkString(", ")}""")

  val orderByIandS: Ordering[Thang] = Ordering.by { x => (x.i, x.s) }
  println(s"""thangs.sorted(orderByIandS) = ${thangs.sorted(orderByIandS).mkString(", ")}""")

  println(s"""thangs.sorted(orderBySandI.reverse) = ${thangs.sorted(orderBySandI.reverse).mkString(", ")}""")
  println(s"""thangs.sorted(orderByIandS.reverse) = ${thangs.sorted(orderByIandS.reverse).mkString(", ")}""")

  val ordering = implicitly[Ordering[(Int, String)]]
  println(s"""ordering.compare( (1, "b"), (1, "a") ) = ${ordering.compare( (1, "b"), (1, "a") )}""")
  println(s"""ordering.compare( (1, "b"), (1, "b") ) = ${ordering.compare( (1, "b"), (1, "b") )}""")
  println(s"""ordering.compare( (1, "b"), (1, "c") ) = ${ordering.compare( (1, "b"), (1, "c") )}""")
}

object PersonSorting extends App {
  import scala.language.implicitConversions

  case class Person(name: String)

  val array = Array(Person("Chloe"), Person("Andrea"), Person("BeeKay"))

  class OrderedPerson(val person: Person) extends AnyVal with Ordered[Person] {
    def compare(that: Person) = person.name.compare(that.name)
  }

  implicit def personToOrdered(p: Person): OrderedPerson = new OrderedPerson(p)

  util.Sorting.quickSort(array)
}
