package object multi {
  val calculatePiFor: Int => Double = (decimals: Int) => {
    var acc = 0.0
    for (i <- 0 until decimals)
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  def factorial(number: BigInt): BigInt = {
    @annotation.tailrec
    def fact(total: BigInt, number: BigInt): BigInt = {
      if (number == BigInt(1))
        total
      else
        fact(total* number, number - 1)
    }
    fact(1, number)
  }

  /** Measure execution time of the given block of code */
  def time[T](msg: String)(block: => T): T = {
    val t0 = System.nanoTime()
    val result: T = block
    val elapsedMs = (System.nanoTime() - t0) / 1000000
    println(s"  Elapsed time for $msg: " + elapsedMs + "ms")
    result
  }
}
