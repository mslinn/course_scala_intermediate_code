package collections

object CollectionOrdered extends App {
  println(s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${List(3, 7, 5, 2).sortWith((x, y) => x < y)}""")
  println(s"""List(3, 7, 5, 2).sortWith(_ < _) = ${List(3, 7, 5, 2).sortWith(_ < _)}""")

  println(s"""thingOrdered1>thingOrdered2 = ${thingOrdered1 > thingOrdered2}""")
  println(s"""thingOrdered1<thingOrdered2 = ${thingOrdered1 < thingOrdered2}""")
  println(s"""thingOrdered1==ThingOrdered(1, "z") = ${thingOrdered1 == new ThingOrdered(1, "z")}""")
  println(s"""thingOrdered1<=thingOrdered2 = ${thingOrdered1 <= thingOrdered2}""")
  println(s"""thingOrdered1>=thingOrdered2 = ${thingOrdered1 >= thingOrdered2}""")

  println(s"""thingsOrdered.sorted = ${thingsOrdered.sorted.mkString(", ")}""")
  println(s"""thingsOrdered before quicksort = ${thingsOrdered.mkString(", ")}""")
  util.Sorting.quickSort(thingsOrdered) // modifies things Array
  println(s"""thingsOrdered after quicksort = ${thingsOrdered.mkString(", ")}""")
  println(s"""thingsOrdered.sortWith((x, y) => x.i > y.i)=${thingsOrdered.sortWith((x, y) => x.i > y.i).mkString(", ")}""")
  println(s"""thingsOrdered.sortWith((x, y) => x.s > y.s)=${thingsOrdered.sortWith((x, y) => x.s > y.s).mkString(", ")}""")
}

object CollectionOrdering extends App {
  println(s"""thingOrderings = ${thingOrderings.mkString(", ")}""")
  println(s"""thingOrderings.sorted = ${thingOrderings.sorted.mkString(", ")}""")
  println(s"""thingOrderings.sorted(Ordering[ThingOrdering]) = ${thingOrderings.sorted(Ordering[ThingOrdering]).mkString(", ")}""")

  val orderByI: Ordering[ThingOrdering] = Ordering.by { _.i }
  println(s"""thingOrderings.sorted(orderByI) = ${thingOrderings.sorted(orderByI).mkString(", ")}""")

  val orderByIReverse: Ordering[ThingOrdering] = orderByI.reverse
  println(s"""thingOrderings.sorted(orderByIReverse) = ${thingOrderings.sorted(orderByIReverse).mkString(", ")}""")

  val orderByS: Ordering[ThingOrdering] = Ordering.by { _.s }
  println(s"""thingOrderings.sorted(orderByS) = ${thingOrderings.sorted(orderByS).mkString(", ")}""")

  val orderBySReverse: Ordering[ThingOrdering] = orderByS.reverse
  println(s"""thingOrderings.sorted(orderBySReverse) = ${thingOrderings.sorted(orderBySReverse).mkString(", ")}""")

  val orderBySandI: Ordering[ThingOrdering] = Ordering.by { x => (x.s, x.i) }
  println(s"""thingOrderings.sorted(orderBySandI) = ${thingOrderings.sorted(orderBySandI).mkString(", ")}""")

  val orderByIandS: Ordering[ThingOrdering] = Ordering.by { x => (x.i, x.s) }
  println(s"""thingOrderings.sorted(orderByIandS) = ${thingOrderings.sorted(orderByIandS).mkString(", ")}""")

  println(s"""thingOrderings.sorted(orderBySandI.reverse) = ${thingOrderings.sorted(orderBySandI.reverse).mkString(", ")}""")
  println(s"""thingOrderings.sorted(orderByIandS.reverse) = ${thingOrderings.sorted(orderByIandS.reverse).mkString(", ")}""")

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
