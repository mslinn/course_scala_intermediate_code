package solutions

object MemoProd extends App with moar.Memoize {
  val f1: Int => Int = memoize(moar.Memo.method1)
  val f2: Int => Int = memoize(moar.Memo.method2)

  // Compute values; side effect: memoizes results into caches
  1 to 3 foreach { i =>
    println(s"f1(i)=${f1(i)}")
    println(s"f2(i)=${f2(i)}")
  }

  // Fetch first 3 results from memo caches and compute the other values
  1 to 6 foreach { i =>
    println(s"f1(i)=${f1(i)}")
    println(s"f2(i)=${f2(i)}")
  }
}
