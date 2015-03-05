package moar

object Memo extends App with MemoizeBaby {
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

  val f1: (Int) => Int = memoize(method1, "f1") // lift method1 into a Function1 instance and memoize it
  val f2: (Int) => Int = memoize(method2, "f2") // lift method2 into a Function1 instance and memoize it

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

trait MemoizeBaby {
  /** Version just for learning.
    * Transform given `Function1` instance into another `Function1` instance backed by a `WeakHashMap` for memoization of parameter/result pairs.
    * @param f Must be `Function1` (single argument)
    * @param name displayed for this memoized `Function``, only used for debugging; remove for production code */
  def memoize[Key, Value](f: Key => Value, name: String="") = {
    /** Each memoized Function has its own cache */
    val cache = collection.mutable.WeakHashMap.empty[Key, Value]
    (key: Key) => {
      if (cache.keySet.contains(key)) println(s"Retrieving $name($key)=${cache(key)}") // comment out for production code
      cache.getOrElseUpdate(key, f(key))
    }
  }
}

trait Memoize {
  /** Transform given `Function1` instance into another `Function1` instance backed by a `WeakHashMap` for memoization of parameter/result pairs.
    * @param f Must be `Function1` (single argument) */
  def memoize[Key, Value](f: Key => Value) = {
    /** Each memoized Function has its own cache */
    val cache = collection.mutable.WeakHashMap.empty[Key, Value]
    (key: Key) => {
      cache.getOrElseUpdate(key, f(key))
    }
  }
}

object MemoizeCurry extends App with Memoize {
  /** method to be memoized */
  def method(i: Int, string: String): String = string * i

  val key1 = 1
  val pf1 = method(key1, _: String)
  val pf2 = method(2, _: String)
  val pf42 = method(key1*42, _: String)

  pf1("hi ")
  pf2("bye ")
  pf42("42 ")

  val memoizedF1 = memoize(pf1)
  val memoizedF2 = memoize(pf2)
  val memoizedF42 = memoize(pf42)

  memoizedF1("hi ")
  memoizedF1("bye ")
  memoizedF1("42 ")
}
