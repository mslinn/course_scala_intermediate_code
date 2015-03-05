package collections

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

