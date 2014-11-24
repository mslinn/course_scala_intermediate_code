package solutions

class Memoize[-T, +R](f: T => R) extends (T => R) {
  private[this] val vals = collection.mutable.Map.empty[T, R]

  def apply(x: T): R = {
    if (vals.keySet.contains(x)) {
      vals(x)
    } else {
      val y = f(x)
      vals += x -> y
      y
    }
  }
}

object Memoize {
  def apply[T, R](f: T => R) = new Memoize(f)
}


object MemoDemo extends App {
  val calculatePiFor: Int => Double = (decimals: Int) => {
      var acc = 0.0
      for (i <- 0 until decimals)
        acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
      acc
    }

  /** Measure execution time of the given block of code */
  def time[T](msg: String)(block: => T): T = {
    val t0 = System.nanoTime()
    val result: T = block
    val elapsedMs = (System.nanoTime() - t0) / 1000000
    println(s"  Elapsed time for $msg: " + elapsedMs + "ms")
    result
  }

  val piMemoized = Memoize(calculatePiFor)

  def doIt(n: Int): Double = time(s"pi to $n decimals")(piMemoized(n))

  doIt(2000000)
  doIt(2000000)
  doIt(3000000)
  doIt(3000000)
}
