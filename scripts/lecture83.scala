def time[T](block: => T): T = {
  val t0 = System.nanoTime()
  val result: T = block
  val elapsedMs = (System.nanoTime() - t0) / 1000000
  println("Elapsed time: " + elapsedMs + "ms")
  result
}
def calculatePiFor(decimalPlaces: Int): Double = {
  var acc = 0.0
  for (i <- 0 until decimalPlaces)
    acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
  acc
}
time(calculatePiFor(100000))
time[Double](calculatePiFor(100000))
import io.Source
time(Source.fromURL("http://scalacourses.com").trim)
case class Thing(val i: Int, val s: String) extends Ordered[Thing] {
  def compare(that: Thing) =  {
    val primaryKey = this.i - that.i
    if (primaryKey!=0) primaryKey else this.s compare that.s
  }

  override def equals(other: Any) = {
    val that = other.asInstanceOf[Thing]
    this.i == that.i && this.s==that.s
  }

  override def hashCode = super.hashCode
}
val thing1 = Thing(1, "z")
val thing2 = Thing(2, "y")
val thing3 = Thing(3, "x")
thing1>thing2
thing1<thing2
thing1 == Thing(1, "z")
thing1==thing2
thing1<=thing2
thing1>=thing2
val things = Array(thing2, thing1, thing3)
things.sorted
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
} object Thang {
  implicit val ThangOrdering = Ordering.by { thang: Thang => 
    (thang.i, thang.s) 
  }
}
val orderByI = Ordering.by { thang: Thang => thang.i }
val orderByS = Ordering.by { thang: Thang => thang.s }
val thangs = Array(new Thang(1, "x"), new Thang (33, "b"), new Thang(4, "m"))
thangs.sorted(orderByI)
thangs.sorted(orderByS)
val thang1 = new Thang(1, "x")
val thang2 = new Thang (33, "b")
val thang3 = new Thang(4, "m")
class Thung(override val i: Int, override val s: String) extends Thang(i, s)
val thung1 = new Thung(12, "ab")
val thung2 = new Thung(99, "xy")
import collection.mutable.SortedSet
SortedSet(thang1, thang2, thang3)
SortedSet(thang1, thang2, thang3)(orderByS)
SortedSet[Thang](thung1, thang2, thung2)
val ss: SortedSet[Thang] = SortedSet(thung1, thang2, thung2)
SortedSet[Thang](thung1, thang2, thung2)(orderByI)
import scala.collection.mutable.{HashMap, HashSet, WeakHashMap}
val map = HashMap.empty[Int, String]
map += (1 -> "Buy a dog")
map += (2 -> "Sell the cat")
map(1)
map contains 2
WeakHashMap(1 -> thang1, 33 -> thang2, 4 -> thang3)
import collection.mutable.{Queue, Stack, ArrayStack}
val queue = Queue.empty[String]
queue += "asdf"
queue += "qwer"
queue.dequeue
queue
val stack1 = Stack(thang1, thang2, thang3)
val stack2 = ArrayStack(new Thang(1, "x"), new Thang (33, "b"), new Thang(4, "m"))
import collection.mutable.{LinkedHashMap, LinkedHashSet, LinkedList, DoubleLinkedList}
val llist = LinkedList(1)
class Thung(override val i: Int, override val s: String) extends Thang(i, s)
class Th_ngMunger[A <: Thang](th_ng: A) {
  def doYourThing(count: Int) = s"${th_ng.i * count}: ${th_ng.s * count}"
}
new Th_ngMunger(thang1).doYourThing(3)
new Th_ngMunger(thung1).doYourThing(5)
class Bag[+T] {
  val ml = collection.mutable.MutableList.empty[T]

  def put(item: T) = ml += item
}
class [+T <: Thang] Thoon { /* etc */ }
import collection.mutable

class Thang(val i: Int, val s: String) {
  override def toString() = s"Thang $i: $s"
}

class Thung(override val i: Int, override val s: String) extends Thang(i, s) {
  override def toString() = s"Thung $i: $s"
}

class Bag[+T <: Thang] {
  val ml = mutable.MutableList.empty[Thang]
  // this would be threadsafe, but slower:
  //val mb = new ArrayBuffer[Thang] with SynchronizedBuffer[Thang]

  def put[U >: T <: Thang](item: U): Unit = ml += item

  def findByI(i: Int): List[Thang] = ml.filter(_.i==i).toList

  def findByS(s: String): List[Thang] = ml.filter(_.s==s).toList
}

object Main extends App {
  val bag = new Bag[Thung]
  bag.put(new Thang(2, "abc"))
  bag.put(new Thung(3, "def"))
  bag.put(new Thang(4, "xyz"))
  println(bag.findByI(4))
  println(bag.findByS("def"))
}

