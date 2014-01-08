object ExtendJavaSet extends App {

  trait IgnoredCaseSet[T] extends java.util.Set[T] {
    abstract override def add(t: T): Boolean =
      t match {
        case string: String => super.add(string.toLowerCase.asInstanceOf[T])

        case obj => super.add(obj)
      }

    abstract override def contains(obj: Object) =
      obj match {
        case s: String =>
          super.contains(s.toLowerCase)

        case o =>
          super.contains(o)
      }
  }

  class MySet extends java.util.HashSet[String] with IgnoredCaseSet[String]

  val mySet = new MySet() // Java sets are mutable, only the reference is immutable
  mySet.add("One")
  mySet.add("Two")
  mySet.add("Three")
  println(s"mySet=$mySet")
}

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

  override def toString() = s"Thang $i: $s"
}

object Thang {
  implicit val ThangOrdering = Ordering.by { thang: Thang =>
    (thang.i, thang.s)
  }
}

class Thung(override val i: Int, override val s: String) extends Thang(i, s) {
  override def toString() = s"Thung $i: $s"
}


class Th_ngMunger[A <: Thang](th_ng: A) {
  def doYourThing(count: Int) = s"${th_ng.i * count}: ${th_ng.s * count}"
}

object Main1 extends App {
  val thang = new Thang(4, "a")
  val thung = new Thung(5, "b")

  new Th_ngMunger(thang).doYourThing(3)
  new Th_ngMunger(thung).doYourThing(5)
}

import collection.mutable

class Bag[+T <: Thang] {
  val ml = mutable.MutableList.empty[Thang]
  def put[U >: T <: Thang](item: U): Unit = ml += item
  def findByI(i: Int): List[Thang] = ml.filter(_.i==i).toList
  def findByS(s: String): List[Thang] = ml.filter(_.s==s).toList
}

object Main2 extends App {
  val bag = new Bag[Thung] // bag can hold Thungs and Thangs
  bag.put(new Thang(2, "abc"))
  bag.put(new Thung(3, "def"))
  bag.put(new Thang(4, "xyz"))
  println(bag.findByI(4))
  println(bag.findByS("def"))
}

