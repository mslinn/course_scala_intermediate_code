import collection._

object CollectionMutableFun extends App {
  println(s"""List(1, 2, 3).toBuffer = ${List(1, 2, 3).toBuffer}""")

  val lb = mutable.ListBuffer(1, 2, 3)
  println(s"""lb(0) * 3 = ${lb(0) * 3}""")
  println(s"""lb.toArray = ${lb.toArray}""")
  println(s"""lb.toSeq = ${lb.toSeq}""")
  println(s"""lb.toIndexedSeq = ${lb.toIndexedSeq}""")

  val mb = new mutable.ArrayBuffer[String] with mutable.SynchronizedBuffer[String]

  class SynchronizedArrayBuffer[T] extends mutable.ArrayBuffer[T] with mutable.SynchronizedBuffer[T]
  val mb2 = new SynchronizedArrayBuffer[String]


  println(s"""mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four") = ${mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four")}""")

  import java.util.concurrent.ConcurrentHashMap
  import collection.JavaConverters._
  val cmap = new ConcurrentHashMap[String, String].asScala

  import collection.concurrent.TrieMap
  val tm = TrieMap.empty[String, String].withDefaultValue("default")
  tm.put("key1", "value1")
  tm.put("key2", "value2")
  println(s"""tm.get("key1") = ${tm.get("key1")}""")
  println(s"""tm("key1") = ${tm("key1")}""")
  println(s"""tm("x") = ${tm("x")}""")

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
