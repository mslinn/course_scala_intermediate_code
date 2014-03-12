import collection._
import collection.JavaConverters._

object CollectionConverters extends App {
  println(s"""List(1, 2, 3).toArray = ${List(1, 2, 3).toArray}""")
  println(s"""Vector(1, 2, 3).toStream = ${Vector(1, 2, 3).toStream}""")

  println(s"""implicitly[Option[Int] => Iterable[Int]] = ${implicitly[Option[Int] => Iterable[Int]]}""")
  println(s"""implicitly[Function[Option[Int], Iterable[Int]]] = ${implicitly[Function[Option[Int], Iterable[Int]]]}""")
  println(s"""implicitly[Some[Int] => Iterable[Int]] = ${implicitly[Some[Int] => Iterable[Int]]}""")

  val sl = mutable.ListBuffer[Int](1, 2, 3)
  val jl: java.util.List[Int] = sl.asJava   // the type of j1 need not be declared, merely added for clarity
  val s2: mutable.Buffer[Int] = jl.asScala  // the type of s2 need not be declared, merely added for clarity
  assert(sl eq s2)

  val i1 = Iterable(1, 2, 3)
  val i2 = i1.asJava.asScala
  assert(i1 eq i2)

  val m1 = mutable.HashMap(1 -> "eh", 2 -> "bee", 3 -> "sea")
  val m2 = m1.asJava.asScala
  assert (m1 eq m2)
}
