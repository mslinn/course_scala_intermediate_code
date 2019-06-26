package collections

import com.micronautics.utils
import scala.collection._

object CollectionFun extends App {
  val a1 = Array(1, 2, 3)
  println(s"a1=${ a1.mkString(", ") }")

  val a2 = Array.empty[Int]
  val a3: Array[Int] = Array(1, 2, 3)

  a1(0) = a1(0) + 1
  println(s"a1=${ a1.mkString(", ") }")
  a1.update(0, a1(0) + 1)
  println(s"a1=${ a1.mkString(", ") }")

  val chars: Seq[Char] = utils.read("build.sbt").toList
  val x1: String = chars.mkString
  val x2: String = chars.mkString(", ")
  val x3: String = chars.mkString(">>>", ", ", "<<<")

  val immutableMap: immutable.Map[Int, String] = immutable.HashMap.empty[Int, String]
  val mutableMap: mutable.Map[Int, String] = mutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" )
  val map: immutable.Map[Int, String] = immutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" )
  println(s"map.get(1) = ${ map.get(1) }")
  println(s"map(1) = ${ map(1) }")
  println(s"map.get(0) = ${ map.get(0) }")
  //println(s"map(0) = ${map(0)}")  // java.util.NoSuchElementException: key not found: 0
  println(s"""map.getOrElse(0, Some("defaultValue")) = ${ map.getOrElse(0, Some("defaultValue")) }""")

  val map2: mutable.Map[Int, String] = mutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea").withDefaultValue("eh")
  println(s"map2(0) = ${ map2(0) }")

  val emptyMap: immutable.Map[Int, String] = immutable.HashMap.empty
  println(s"""map + (3 -> "c") = ${ map + (3 -> "c") }""")

  println(s"""map + (3 -> "c", 4 -> "d") = ${ map + (3 -> "c", 4 -> "d") }""")
  val map3: immutable.Map[Int, String] = map ++ map2
  println(s"""map ++ map2 = $map3""")
  println(s"""map3.keys = ${ map3.keys }""")
  println(s"""map3.updated(1, "q") = ${ map3.updated(1, "q") }""")
  println(s"""map3.isDefinedAt(42) = ${ map3.isDefinedAt(42) }""")
  println(s"""map3.isDefinedAt(2) = ${ map3.isDefinedAt(2) }""")

  println(s"""immutable.HashSet(1, 1, 2) = ${ immutable.HashSet(1, 1, 2) }""")
  println(s"""mutable.HashSet(1, 1, 2) = ${ mutable.HashSet(1, 1, 2) }""")
  println(s"""mutable.LinkedHashSet(4, 4, 4) = ${ mutable.LinkedHashSet(4, 4, 4) }""")

  val mapFruit = Map(1 -> "apricot", 2 -> "banana", 3 -> "clementine", 4 -> "durian", 5 -> "fig", 6 -> "guava", 7 -> "jackfruit", 8 -> "kiwi", 9 -> "lime", 10 -> "mango")
  println(s"""mapFruit.mapValues(_ capitalize) = ${ mapFruit.view.mapValues(_.capitalize) }""")

  val set2 = mutable.HashSet.empty[String]
  set2 += "Buy a dog"
  set2 += "Sell the cat"
  println(s"set2 = $set2")

  val emptyImmutableBits = immutable.BitSet.empty
  val emptyMutableBits = mutable.BitSet.empty

  val primeBits = immutable.BitSet(2, 3, 5, 7, 11)
  val evenBits =  immutable.BitSet(0, 2, 4, 6, 8, 10)

  val evenSet = Set(0, 2, 4, 6, 8, 10)
  val primeList = List(19, 23, 29)

  println(s"""primeBits & evenBits = ${ primeBits & evenBits }""")
  println(s"""primeBits & evenSet = ${ primeBits & evenSet }""")
  println(s"""primeBits &~ evenBits = ${ primeBits &~ evenBits }""")
  println(s"""primeBits &~ evenSet = ${ primeBits &~ evenSet }""")

  val morePrimes = primeBits + 13 + 17
  println(s"""morePrimes = $morePrimes""")
  println(s"""morePrimes ++ primeList = ${ morePrimes ++ primeList }""")
  println(s"""morePrimes - 11 = ${ morePrimes - 11 }""")
  println(s"""morePrimes -- evenBits = ${ morePrimes -- evenBits }""")

  def doSomething(set: Set[Int]): Unit = println(set.mkString(", "))

  println(s"""doSomething(mutable.HashSet(1, 2, 3)) = ${ doSomething(mutable.HashSet(1, 2, 3)) }""")
  println(s"""doSomething(mutable.LinkedHashSet(1, 2, 3)) = ${ doSomething(mutable.LinkedHashSet(1, 2, 3)) }""")

  println(s"""immutable.HashSet(1.0, 2) = ${ immutable.HashSet[Number](1.0, 2) }""")
  val set: immutable.Set[Number] = immutable.HashSet[Number](1.0, 2)
  println(s"""set = $set""")

  def showMap(message: String, map: Map[Int, String]): Unit = {
    println(s"$message: ")
    map.foreach { case (key, value) =>
      println(s"  key=$key, value=$value")
    }
  }

  // added with Scala 2.13
  showMap("immutable.ListMap", immutable.ListMap(1 -> "One", -2 -> "Negative two", 3 -> "Three"))

  val treeSeqMap: Map[Int, String] = immutable.TreeSeqMap(1 -> "One", -2 -> "Negative two", 3 -> "Three")
  showMap("immutable.TreeSeqMap", treeSeqMap)

  // Custom collection authors need to know about steppers; this course does not address that audience
  val y: IntStepper = treeSeqMap.keysIterator.stepper
  val stepper: IntStepper = treeSeqMap.keyStepper
  val isES = stepper.isInstanceOf[Stepper.EfficientSplit]
  val x: IntStepper = stepper.trySplit()
}
