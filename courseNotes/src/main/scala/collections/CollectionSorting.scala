package collections

object CollectionOrdered extends App {
  println(s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${ List(3, 7, 5, 2).sortWith((x, y) => x < y) }""")
  println(s"""List(3, 7, 5, 2).sortWith(_ < _) = ${ List(3, 7, 5, 2).sortWith(_ < _) }""")

  println(s"""thingOrdered1>thingOrdered2 = ${ thingOrdered1 > thingOrdered2 }""")
  println(s"""thingOrdered1<thingOrdered2 = ${ thingOrdered1 < thingOrdered2 }""")
  println(s"""thingOrdered1==ThingOrdered(1, "z") = ${ thingOrdered1 == new ThingOrdered(1, "z") }""")
  println(s"""thingOrdered1<=thingOrdered2 = ${ thingOrdered1 <= thingOrdered2 }""")
  println(s"""thingOrdered1>=thingOrdered2 = ${ thingOrdered1 >= thingOrdered2 }""")

  println(s"""thingsOrdered.sorted = ${ thingsOrdered.sorted.mkString(", ") }""")
  println(s"""thingsOrdered before quicksort = ${ thingsOrdered.mkString(", ") }""")
  util.Sorting.quickSort(thingsOrdered) // modifies things Array
  println(s"""thingsOrdered after quicksort = ${ thingsOrdered.mkString(", ") }""")
  println(s"""thingsOrdered.sortWith((x, y) => x.i > y.i)=${ thingsOrdered.sortWith((x, y) => x.i > y.i).mkString(", ") }""")
  println(s"""thingsOrdered.sortWith((x, y) => x.s > y.s)=${ thingsOrdered.sortWith((x, y) => x.s > y.s).mkString(", ") }""")
}

object CollectionOrdering extends App {
  println(s"""thingOrderings = ${ thingOrderings.mkString(", ") }""")
  println(s"""thingOrderings.sorted = ${ thingOrderings.sorted.mkString(", ") }""")
  println(s"""thingOrderings.sorted(Ordering[ThingOrdering]) = ${ thingOrderings.sorted(Ordering[ThingOrdering]).mkString(", ") }""")

  println(s"""thingOrderings.sorted(orderByI) =             ${ thingOrderings.sorted(orderByI).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderByIReverse) =      ${ thingOrderings.sorted(orderByIReverse).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderByS) =             ${ thingOrderings.sorted(orderByS).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderBySReverse) =      ${ thingOrderings.sorted(orderBySReverse).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderBySandI) =         ${ thingOrderings.sorted(orderBySandI).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderByIandS) =         ${ thingOrderings.sorted(orderByIandS).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderBySandI.reverse) = ${ thingOrderings.sorted(orderBySandI.reverse).mkString(", ") }""")
  println(s"""thingOrderings.sorted(orderByIandS.reverse) = ${ thingOrderings.sorted(orderByIandS.reverse).mkString(", ") }""")

  val implicitOrdering = implicitly[Ordering[(Int, String)]]
  println(s"""implicitOrdering.compare( (1, "b"), (1, "a") ) = ${ implicitOrdering.compare( (1, "b"), (1, "a") ) }""")
  println(s"""implicitOrdering.compare( (1, "b"), (1, "b") ) = ${ implicitOrdering.compare( (1, "b"), (1, "b") ) }""")
  println(s"""implicitOrdering.compare( (1, "b"), (1, "c") ) = ${ implicitOrdering.compare( (1, "b"), (1, "c") ) }""")
}

object PriorityQueueFun extends App {
  import scala.collection.mutable

  println(s"""The items in thingOrderings sorted by the natural implicitOrdering are: ${ thingOrderings.sorted.mkString(", ") }""")
  val pq1 = mutable.PriorityQueue(thingOrderings: _*)
  println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  try {
    println(s"""pq1.dequeue=${ pq1.dequeue() }""")
  } catch {
    case e: Exception => println(s"${ e.getClass.getName }: ${ e.getMessage }")
  }

  println(s"""\nThe items in thingOrderings sorted by the orderByI implicitOrdering are: ${ thingOrderings.sorted(orderByI).mkString(", ") }""")
  val pq2: mutable.PriorityQueue[ThingOrdering] = mutable.PriorityQueue(thingOrderings: _*)(orderByI)
  pq2.dequeueAll.foreach { item: ThingOrdering => println(s"""pq2.dequeue=$item""") }

  println(s"""\nThe items in thingOrderings sorted by the orderByS implicitOrdering are: ${ thingOrderings.sorted(orderByS).mkString(", ") }""")
  val pq3 = mutable.PriorityQueue(thingOrderings: _*)(orderByS)
  (0 until pq3.length).foreach { _ => println(s"""pq3.dequeue=${pq3.dequeue()}""") }
}

object PersonSorting extends App {
  import scala.language.implicitConversions

  case class Person(name: String)

  val array = Array(Person("Chloe"), Person("Andrea"), Person("BeeKay"))

  class OrderedPerson(val person: Person) extends AnyVal with Ordered[Person] {
    def compare(that: Person): Int = person.name.compare(that.name)
  }

  implicit def personToOrdered(p: Person): OrderedPerson = new OrderedPerson(p)

  util.Sorting.quickSort(array)
}
