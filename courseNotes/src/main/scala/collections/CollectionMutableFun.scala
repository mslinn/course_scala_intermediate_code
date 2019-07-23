package collections

import scala.collection._

object CollectionMutableFun extends App {
  println( s"""List(1, 2, 3).toBuffer = ${List(1, 2, 3).toBuffer}""")

  val lb = mutable.ListBuffer(1, 2, 3)
  println( s"""lb(0) * 3 = ${ lb(0) * 3 }""")
  println( s"""lb.toArray = ${ lb.toArray }""")
  println( s"""lb.toSeq = ${ lb.toSeq }""")
  println( s"""lb.toIndexedSeq = ${ lb.toIndexedSeq }""")

  println( s"""mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four") = ${mutable.WeakHashMap(1 -> "one", 33 -> "thirty-three", 4 -> "four")}""")

  val queue = mutable.Queue.empty[String]
  queue += "asdf"
  queue += "qwer"
  println(s"""queue.dequeue = ${ queue.dequeue() }""")
  println(s"""queue = $queue""")

  println(s"""mutable.ListBuffer(1) = ${ mutable.ListBuffer(1) }""")
  println(s"""mutable.LinkedHashSet(1) = ${ mutable.LinkedHashSet(1)} """)
  println(s"""mutable.LinkedHashMap(1 -> "one") = ${ mutable.LinkedHashMap(1 -> "one") }""")
}

object CMap extends App {
  import scala.collection.concurrent.{TrieMap, Map => CMap }
  import scala.jdk.CollectionConverters._
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
  println(s"""tm.get("key1") = ${ tm.get("key1") }""")
  println(s"""tm("key1") = ${ tm("key1") }""")
  println(s"""tm("x") = ${ tm("x") }""")


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

object GMap extends App {
  import scala.concurrent.ExecutionContext.Implicits._
  type Key = String
  type Value = String

  def defaultValue(key: Key): Value = s"$key value"

  val gCache = TimedCache[Key, Value]()

  val x: Value = gCache.getWithDefault("2", defaultValue("2")) // computes defaultValue
  val y: Value = gCache.getWithDefault("2", defaultValue("2")) // uses cached defaultValue
}

class TimedCache[Key<:Object, Value<:Object](val concurrencyLevel: Int=4, val timeoutMinutes: Int=5)
                                            (implicit ec: scala.concurrent.ExecutionContext) {
  import java.util.concurrent.{Callable, TimeUnit}
  import com.google.common.cache.{Cache, CacheBuilder}
  import scala.concurrent.Future

  lazy val underlying: Cache[Key, Value] = CacheBuilder.newBuilder()
    .concurrencyLevel(concurrencyLevel)
    .softValues()
    .expireAfterAccess(timeoutMinutes, TimeUnit.MINUTES)
    .build[Key, Value]

  @inline def getWithDefault(key: Key, defaultValue: => Value): Value = underlying.get(key,
    new Callable[Value] {
      override def call: Value = defaultValue
    }
  )

  @inline def getAsyncWithDefault(key: Key, defaultValue: => Value): Future[Value] =
    Future { getWithDefault(key, defaultValue) }

  @inline def put(key: Key, value: Value): Unit = underlying.put(key, value)

  @inline def putAsync(key: Key, value: => Value): Future[Unit] = Future { underlying.put(key, value) }
}

object TimedCache {
  @inline def apply[Key<:Object, Value<:Object](concurrencyLevel: Int=4, timeoutMinutes: Int=5)
                                               (implicit ec: scala.concurrent.ExecutionContext): TimedCache[Key, Value] =
    new TimedCache[Key, Value](concurrencyLevel, timeoutMinutes){}
}
