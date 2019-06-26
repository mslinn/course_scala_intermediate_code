package collections

import collection._
import scala.jdk.CollectionConverters._
import scala.collection.parallel.CollectionConverters._
import java.util.{List => JList, Map => JMap, Set => JSet}
import com.micronautics.utils._

object CollectionConverters extends App {
  println(s"""List(1, 1, 2, 2, 3, 3).toArray = ${ List(1, 1, 2, 2, 3, 3).toArray }""")
  println(s"""Vector(1, 1, 2, 2, 3, 3).toStream = ${ Vector(1, 1, 2, 2, 3, 3).to(LazyList) }""")

  println(s"""readLines("build.sbt")=${ readLines("build.sbt") }""")

  println(s"""List(1, 1, 2, 2, 3, 3).to[Set] = ${ List(1, 1, 2, 2, 3, 3).toSet }""")
  println(s"""List(1, 1, 2, 2, 3, 3).to[collection.parallel.ParSet] = ${ List(1, 1, 2, 2, 3, 3).par.toSet }""")

  val list1 = mutable.ListBuffer(1, 2, 3)
  val jl: java.util.List[Int] = list1.asJava   // the type of j1 need not be declared, merely added for clarity
  val s1: mutable.Buffer[Int] = jl.asScala     // the type of s1 need not be declared, merely added for clarity
  assert(list1 eq s1)

  val i1 = Iterator(1, 2, 3)
  val i2 = i1.asJava.asScala
  assert(i1 eq i2)

  val m1 = mutable.HashMap(1 -> "eh", 2 -> "bee", 3 -> "sea")
  val m2 = m1.asJava.asScala
  assert(m1 eq m2)

  val list2: Seq[Int] = List(1, 2, 3)
  val j2: JList[Int] = list2.asJava            // the type of j2 need not be declared, merely added for clarity
  val s2: mutable.Buffer[Int] = j2.asScala     // the type of s2 need not be declared, merely added for clarity
  assert(list2 ne s2)

  val set = immutable.HashSet(1, 2, 3)
  val jSet: JSet[Int] = set.asJava
  val sSet: mutable.Set[Int] = set.asJava.asScala
  assert(set.to(Set) ne sSet)

  val map = immutable.HashMap(2 -> "bee", 1 -> "eh", 3 -> "sea")
  val jMap: JMap[Int, String] = map.asJava
  val sMap: mutable.Map[Int, String] = jMap.asScala
  assert(map.to(Map) ne sMap)

  val treeMap = immutable.TreeMap(2 -> "bee", 1 -> "eh", 3 -> "sea")
  val jMap2: JMap[Int, String] = treeMap.asJava
  val sMap2: mutable.Map[Int, String] = jMap2.asScala
  assert(treeMap ne sMap2.toMap)

  println(s"""implicitly[Option[Int] => Iterable[Int]] = ${ implicitly[Option[Int] => Iterable[Int]] }""")
  println(s"""implicitly[Function[Option[Int], Iterable[Int]]] = ${ implicitly[Function[Option[Int], Iterable[Int]]] }""")
  println(s"""implicitly[Some[Int] => Iterable[Int]] = ${ implicitly[Some[Int] => Iterable[Int]] }""")
}
