import scala.collection._

object CollectionMutableFun extends App {
  println( s"""List(1, 2, 3).toBuffer = ${List(1, 2, 3).toBuffer}""")

  val lb = mutable.ListBuffer(1, 2, 3)
  println( s"""lb(0) * 3 = ${lb(0) * 3}""")
  println( s"""lb.toArray = ${lb.toArray}""")
  println( s"""lb.toSeq = ${lb.toSeq}""")
  println( s"""lb.toIndexedSeq = ${lb.toIndexedSeq}""")

  val mb = new mutable.ArrayBuffer[String] with mutable.SynchronizedBuffer[String]

  class SynchronizedArrayBuffer[T] extends mutable.ArrayBuffer[T] with mutable.SynchronizedBuffer[T]

  val mb2 = new SynchronizedArrayBuffer[String]


  println( s"""mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four") = ${mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four")}""")


  val queue = mutable.Queue.empty[String]
  queue += "asdf"
  queue += "qwer"
  println(s"""queue.dequeue = ${queue.dequeue()}""")
  println(s"""queue = $queue""")

  println(s"""mutable.LinkedList(1) = ${mutable.LinkedList(1)}""")
  println(s"""mutable.LinkedHashSet(1) = ${mutable.LinkedHashSet(1)}""")
  println(s"""mutable.LinkedHashMap(1 -> "one") = ${mutable.LinkedHashMap(1 -> "one")}""")
  println(s"""mutable.DoubleLinkedList(1 -> "two") = ${mutable.DoubleLinkedList(1 -> "two")}""")
}

object CMap extends App {
  import collection.concurrent.{TrieMap, Map => CMap }
  import collection.JavaConverters._
  import java.util.concurrent.{ConcurrentSkipListMap, ConcurrentHashMap => JConcurrentHashMap }

  val cmap = new JConcurrentHashMap[String, String].asScala
  println(s"cmap = $cmap")

  val cslm = new ConcurrentSkipListMap[String, String].asScala
  println(s"""cslm = $cslm""")

  val cslm2 = new ConcurrentSkipListMap[String, String].asScala.withDefaultValue("default")
  println(s"""cslm2("") = $cslm2("")""")


  val tm = TrieMap.empty[String, String].withDefaultValue("default")
  tm.put("key1", "value1")
  tm.put("key2", "value2")
  println(s"""tm.get("key1") = ${tm.get("key1")}""")
  println(s"""tm("key1") = ${tm("key1")}""")
  println(s"""tm("x") = ${tm("x")}""")


  def ensureKeyIsPresent[U, V](map: CMap[U, V], key: U, value: V): CMap[U, V] = {
    map.putIfAbsent(key, value)
    map
  }

  val cmap2 = ensureKeyIsPresent(new JConcurrentHashMap[String, String].asScala, "key", "value")

  val cmap3 = ensureKeyIsPresent(new TrieMap, "key", "value")
  println(s"cmap3 = $cmap3")

  val cmap4 = ensureKeyIsPresent(new ConcurrentSkipListMap().asScala, "key", "value")
  println(s"cmap4 = $cmap4")

  val cmap5 = ensureKeyIsPresent(new JConcurrentHashMap().asScala, "key", "value")
  println(s"cmap5 = $cmap5")
}

object Memo extends App with Memoize {

  /** method to be memoized */
  def method1(i: Int): Int = {
    val result = i * 2 + 1
    println(s"Computing f1($i)=$result")
    result
  }

  /** method to be memoized */
  def method2(i: Int): Int = {
    val result = i * 6 - 21
    println(s"Computing f2($i)=$result")
    result
  }

  val f1 = memoize(method1, "f1") // lift method1 into a Function1 instance and memoize it
  val f2 = memoize(method2, "f2") // lift method2 into a Function1 instance and memoize it

  // Compute values; side effect: memoizes results into caches
  1 to 3 foreach { i =>
    val result1 = f1(i)  // a real program would do something with result1
    val result2 = f2(i)  // a real program would do something with result2
  }

  // Fetch first 3 results from memo caches and compute the other values
  1 to 6 foreach { i =>
    val result1 = f1(i)  // a real program would do something with result1
    val result2 = f2(i)  // a real program would do something with result2
  }
}

trait Memoize {
  /** Transform given Function1 instance into another Function1 instance backed by a WeakHashMap for memoization of parameter/result pairs.
    * @param f Must be Function1 (single argument)
    * @param name displayed for this memoized Function, only used for debugging; remove for production code */
  def memoize[Key, Value](f: Key => Value, name: String="") = {
    /** Each memoized Function has its own cache */
    val cache = collection.mutable.WeakHashMap.empty[Key, Value]
    (key: Key) => {
      if (cache.keySet.contains(key)) println(s"Retrieving $name($key)=${cache(key)}") // comment out for production code
      cache.getOrElseUpdate(key, f(key))
    }
  }
}
