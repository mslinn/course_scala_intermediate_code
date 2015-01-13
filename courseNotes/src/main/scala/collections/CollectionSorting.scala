package collections

object CollectionOrdered extends App {
  println(s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${List(3, 7, 5, 2).sortWith((x, y) => x < y)}""")
  println(s"""List(3, 7, 5, 2).sortWith(_ < _) = ${List(3, 7, 5, 2).sortWith(_ < _)}""")

  println(s"""thing1>thing2 = ${thing1 > thing2}""")
  println(s"""thing1<thing2 = ${thing1 < thing2}""")
  println(s"""thing1==Thing(1, "z") = ${thing1 == new Thing(1, "z")}""")
  println(s"""thing1<=thing2 = ${thing1 <= thing2}""")
  println(s"""thing1>=thing2 = ${thing1 >= thing2}""")

  println(s"""things.sorted = ${things.sorted.mkString(", ")}""")
  println(s"""things before quicksort = ${things.mkString(", ")}""")
  util.Sorting.quickSort(things) // modifies things Array
  println(s"""things after quicksort = ${things.mkString(", ")}""")
  println(s"""things.sortWith((x, y) => x.i > y.i)=${things.sortWith((x, y) => x.i > y.i).mkString(", ")}""")
  println(s"""things.sortWith((x, y) => x.s > y.s)=${things.sortWith((x, y) => x.s > y.s).mkString(", ")}""")
}

object CollectionOrdering extends App {
  println(s"""things = ${things.mkString(", ")}""")
  println(s"""things.sorted = ${things.sorted.mkString(", ")}""")
  println(s"""things.sorted(Ordering[Thang]) = ${things.sorted(Ordering[Thing]).mkString(", ")}""")

  val orderByI: Ordering[Thing] = Ordering.by { _.i }
  println(s"""things.sorted(orderByI) = ${things.sorted(orderByI).mkString(", ")}""")

  val orderByIReverse: Ordering[Thing] = orderByI.reverse
  println(s"""things.sorted(orderByIReverse) = ${things.sorted(orderByIReverse).mkString(", ")}""")

  val orderByS: Ordering[Thing] = Ordering.by { _.s }
  println(s"""things.sorted(orderByS) = ${things.sorted(orderByS).mkString(", ")}""")

  val orderBySReverse: Ordering[Thing] = orderByS.reverse
  println(s"""things.sorted(orderBySReverse) = ${things.sorted(orderBySReverse).mkString(", ")}""")

  val orderBySandI: Ordering[Thing] = Ordering.by { x => (x.s, x.i) }
  println(s"""things.sorted(orderBySandI) = ${things.sorted(orderBySandI).mkString(", ")}""")

  val orderByIandS: Ordering[Thing] = Ordering.by { x => (x.i, x.s) }
  println(s"""things.sorted(orderByIandS) = ${things.sorted(orderByIandS).mkString(", ")}""")

  println(s"""things.sorted(orderBySandI.reverse) = ${things.sorted(orderBySandI.reverse).mkString(", ")}""")
  println(s"""things.sorted(orderByIandS.reverse) = ${things.sorted(orderByIandS.reverse).mkString(", ")}""")

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
