package collections

import com.micronautics.utils
import scala.collection._

object SetFun extends App {
  val immSet: immutable.Set[Int] = immutable.Set(0, 2, 4, 6, 8, 10)
  println(s"""immSet = $immSet""")

  val mutSet: mutable.Set[Int]   = mutable.Set(2, 3, 5, 7, 11, 13)
  println(s"""mutSet = $mutSet""")

  mutSet += 123
  println(s"""mutSet after adding 123 = $mutSet""")

  mutSet.addOne(23).addOne(29)
  println(s"""mutSet after adding 23 and 29 = $mutSet""")

  val largerImmSet = immSet.incl(8).incl(10)
  println(s"""largerImmSet = $largerImmSet""")

  mutSet -= 123
  println(s"""mutSet after removing 123 = $mutSet""")

  mutSet.subtractOne(23).subtractOne(29)
  println(s"""mutSet after removing 23 and 29 = $mutSet""")

  val smallerImmSet = immSet.excl(8).excl(10)
  println(s"""smallerImmSet = $smallerImmSet""")

  println(s"mutSet & immSet = ${ mutSet & immSet }")
  println(s"immSet & mutSet = ${ immSet & mutSet }")
  println(s"mutSet &~ immSet = ${ mutSet &~ immSet }")
  println(s"immSet &~ mutSet = ${ immSet &~ mutSet }")

  println(s"mutSet ++ List(123) = ${ mutSet ++ List(123) }")
  println(s"immSet ++ List(123) = ${ immSet ++ List(123) }")
  println(s"immSet.removedAll(mutSet) = ${ immSet.removedAll(mutSet) }")

  val immHashSet = immutable.HashSet(2, 5, 1)
  val mutHashSet = mutable.HashSet(2, 5, 1)

  println(s"mutable.HashSet(4, 2, 4) = ${ mutable.HashSet(4, 2, 4) }")
  println(s"mutable.LinkedHashSet(4, 2, 4) = ${ mutable.LinkedHashSet(4, 2, 4) }")
}

object BitSetFun extends App {
  val emptyMutableBits = mutable.BitSet.empty
  val emptyImmutableBits = immutable.BitSet.empty

  val immBits = immutable.BitSet(2, 3, 5, 7, 11)
  val mutBits =  mutable.BitSet(0, 2, 4, 6, 8, 10)
  println(s"immBits ++ mutBits = ${ immBits ++ mutBits }")
}

trait MapHelper {
  def showMap(message: String, map: Map[Int, String]): Unit = {
    println(s"$message: ")
    map.foreach { case (key, value) =>
      println(s"  key=$key, value=$value")
    }
  }
}

object MapFun extends App with MapHelper {
  val immMap: immutable.HashMap[Int, String] = immutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" )
  val immEmpty1 = immutable.HashMap.empty
  val mutEmpty = mutable.HashMap.empty

  val immEmpty2: immutable.HashMap[Int, String] = immutable.HashMap.empty
  val immEmpty3 = immutable.HashMap.empty[Int, String]

  println(s"immMap.get(1) = ${ immMap.get(1) }")
  println(s"immMap(1) = ${ immMap(1) }")

  println(s"immMap.get(0) = ${ immMap.get(0) }")
  try {
    println(s"immMap(0) = ${ immMap(0) }")
  } catch {
    case e: Exception => println(s"immMap(0) threw ${ e.getMessage }")
  }

  val mutMap = mutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea").withDefaultValue("eh")
  println(s"mutMap(0) = ${ mutMap(0) }")
  println(s"""mutMap.getOrElse(0, "defaultValue") = ${ mutMap.getOrElse(0, "defaultValue") }""")

  mutMap += (3 -> "c")
  showMap("mutMap after adding a tuple", mutMap)

  mutMap.addOne(3 -> "c")
  showMap("mutMap after adding another tuple", mutMap)

  mutMap ++= Map(3 -> "c2", 4 -> "d")
  showMap("mutMap after adding another Map", mutMap)

  showMap("mutMap ++ immMap", mutMap ++ immMap)
  showMap("mutMap ++ immMap", mutMap ++ immMap)
  showMap("""mutMap.addAll(Map(3 -> "c2", 4 -> "d"))""", mutMap.addAll(Map(3 -> "c2", 4 -> "d")))

  println(s"""mutMap.keys = ${ mutMap.keys }""")
  println(s"""mutMap.keySet = ${ mutMap.keySet }""")

  println(s"""mutMap.values = ${ mutMap.values }""")
  println(s"""mutMap.values.toList = ${ mutMap.values.toList }""")

  println(s"""mutMap.isDefinedAt(42) = ${ mutMap.isDefinedAt(42) }""")
  println(s"""immMap.isDefinedAt(42) = ${ immMap.isDefinedAt(42) }""")

  val zippedFruit = immutable.Map(1 -> "apricot", 2 -> "banana", 3 -> "clementine", 4 -> "durian", 5 -> "fig",
                                  6 -> "guava", 7 -> "jackfruit", 8 -> "kiwi", 9 -> "lime", 10 -> "mango")

  println(s"""zippedFruit.mapValues(_.capitalize) = ${ zippedFruit.mapValues(_.capitalize) }""") // Pre-2.13
  showMap("""zippedFruit.view.mapValues(_.capitalize).toMap""", zippedFruit.view.mapValues(_.capitalize).toMap)

  showMap("""immMap.updated(1, "q")""", immMap.updated(1, "q"))

  showMap("""mutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" )""", mutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" ))
  showMap("""immutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" )""", immutable.HashMap( 1 -> "eh", 2 -> "bee", 3 -> "sea" ))
}

object SortFun extends App with MapHelper {
  val immTreeSet = immutable.TreeSet(3, 1, 2)
  val mutTreeSet = mutable.TreeSet(3, 1, 2)
  println("""immutable.TreeSet(3, 1, 2) = """ + immTreeSet)
  println("""mutable.TreeSet(3, 1, 2) = """ + mutTreeSet)

  // I do not like relying on an interface's default implementation
  println("""immutable.SortedSet(3, 1, 2) = """ + immutable.SortedSet(3, 1, 2))
  println("""mutable.SortedSet(3, 1, 2) = """ + mutable.SortedSet(3, 1, 2))


  val hashSet = immutable.HashSet(1, 2, 3)
  println("""immutable.HashSet(1, 2, 3).to(immutable.TreeSet) = """ + hashSet.to(immutable.TreeSet))
  println("""immutable.HashSet(1, 2, 3).to(mutable.TreeSet) = """ + hashSet.to(mutable.TreeSet))

  println("""immutable.HashSet(1, 2, 3).to(immutable.SortedSet) = """ + hashSet.to(immutable.SortedSet))
  println("""immutable.HashSet(1, 2, 3).to(mutable.SortedSet) = """ + hashSet.to(mutable.SortedSet))

  val tuples: List[(Int, String)] = List(3 -> "c", 2 -> "b", 1 -> "a")
  showMap("""immutable.TreeMap(3 -> "c", 2 -> "b", 1 -> "a")""", immutable.TreeMap(tuples: _*))
  showMap("""mutable.TreeMap(3 -> "c", 2 -> "b", 1 -> "a")""", mutable.TreeMap(tuples: _*))

  // I do not like relying on an interface's default implementation
  showMap("""immutable.SortedMap(3 -> "c", 2 -> "b", 1 -> "a")""", immutable.SortedMap(tuples: _*))
  showMap("""mutable.SortedMap(3 -> "c", 2 -> "b", 1 -> "a")""", mutable.SortedMap(tuples: _*))

  val treeSeqMap: Map[Int, String] = immutable.TreeSeqMap(1 -> "One", -2 -> "Negative two", 3 -> "Three")
  showMap("immutable.TreeSeqMap", treeSeqMap)
}
