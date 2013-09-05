def calculatePiFor(decimals: Int): Double = {
  var acc = 0.0
  for (i <- 0 until decimals)
    acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
  acc
}
  
/** Simulate a CPU-bound task */ 
def cpuIntensive(intensity: Int): Any = { calculatePiFor(intensity) }

/** @param minDelay minimum time (ms) to sleep per invocation
  * @param maxDelay maximum time (ms) to sleep per invocation
  * @param nrOfFetches number of times to repeatedly sleep then run a short computation per invocation */
private def simulateSpider(minDelay: Int, maxDelay: Int,  nrOfFetches: Int) {
  for (i <- 0 until nrOfFetches) {
    // simulate from minDelay to maxDelay ms latency
    Thread.sleep(random.nextInt(maxDelay-minDelay) + minDelay)
    calculatePiFor(0, 50) // simulate a tiny amount of computation
  }
}

/** Simulate an IO-bound task (web spider) */
def ioBound(): Any = simulateSpider(5, 30, fetchCount)

def time(block: => Any): Any = {
  val t0 = System.nanoTime()
  val result: Any = block
  val elapsedMs = (System.nanoTime() - t0) / 1000000
  println("Elapsed time: " + elapsedMs + "ms")
  result
}
time((1 to 10000).toList.map(n => cpuIntensive(n)))
time((1 to 10000).toList.par.map(n => cpuIntensive(n)))
(1 to 10000).toList.par.map { n => cpuIntensive(n) }.filter(_.toString.contains("6")).getClass.getName
(1 to 10000).toList.par.map { n => cpuIntensive(n) }.filter(_.toString.contains("6")).toVector.getClass.getName
(1 to 10000).toList.par.map { n => cpuIntensive(n) }.filter(_.toString.contains("6")).toVector.reduce { (acc, n) => acc + 1 }
(1 to 10000).toList.par.map { n => cpuIntensive(n) }.filter(_.toString.contains("6")).toVector.asInstanceOf[Vector[Double]].reduce { (acc, n) => acc + 1 }
val parList = (1 to 10000).toList.par
val filteredResult = parList.map { n => cpuIntensive(n) }.filter(_.toString.contains("6"))
val answer = filteredResult.toVector.asInstanceOf[Vector[Double]].reduce { (acc, n) => acc + 1 }

