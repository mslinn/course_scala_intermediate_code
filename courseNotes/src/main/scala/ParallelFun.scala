import scala.collection.parallel.ParSeq

object ParallelFun extends App {
  def calculatePiFor(decimals: Int): Double = {
    var acc = 0.0
    for (i <- 0 until decimals)
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  /** Simulate a CPU-bound task */
  def cpuIntensive(intensity: Int): Double = { calculatePiFor(intensity) }

  def parFun = {
    val result: ParSeq[Double] = for {
      i <- (1 to 10000).toList.par
      load = cpuIntensive(i) if load.toString.contains("6")
    } yield load
    result.sum
  }

  println(s"parFun=$parFun")
}
