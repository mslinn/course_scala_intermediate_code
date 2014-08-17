object ParallelFun extends App {
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


  /** Simulate a CPU-bound task */
  class CpuBound(val iterations:Int = 10000) {
    import scala.collection.parallel.immutable.ParSeq

    def goNuts(decimals: Int) = {
      println(s"Starting $iterations CPU-bound computations")
      time("scalar CPU-bound computation") { (1 to iterations).map(_ => calculatePiFor(decimals)) }
      time[ParSeq[Double]]("parallel CPU-bound computation") { (1 to iterations).par.map { _ => calculatePiFor(decimals) } }
    }

    def parSixes: Long = {
      val result: collection.parallel.ParSeq[Long] = {
        import scala.collection.parallel.ForkJoinTaskSupport
        val parRange = (1 to iterations).par
        parRange.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(2))
        for {
          i <- parRange
          load = calculatePiFor(i) if load.toString.contains("6")
        } yield 1L
      }
      result.sum
    }
  }


  /** Simulate an IO-bound task */
  class IoBound {
    val random = util.Random
    val iterations = 10
    val fetchCount = 10

    /** Minimum time (ms) to sleep per invocation */
    val minDelay = 5

    /** maximum time (ms) to sleep per invocation */
    val maxDelay = 30

    val computeRandomDelays = (count: Int) => {
      def randomDelay = random.nextInt(maxDelay-minDelay) + minDelay
      for (i <- 0 until count) yield randomDelay
    }

    val randomDelays = computeRandomDelays(fetchCount)

    /** Simulate an IO-bound task (web spider) */
    val simulateSpider: () => Unit = () => {
      for (i <- 0 until fetchCount) {
        // Simulate a random amount of latency (milliseconds) varying between minDelay and maxDelay
        Thread.sleep(randomDelays(i))
        calculatePiFor(50) // Simulate a tiny amount of computation
      }
      ()
    }

    def goNuts: Unit = {
      println(s"Starting $fetchCount IO-bound computations")
      time("scalar IO-bound computation") {
        (1 to iterations).foreach { _ => simulateSpider()}
      }
      time("parallel IO-bound computation") {
        (1 to iterations).par.foreach { _ => simulateSpider() }
      }
    }
  }

  class AntiDemo {
    def goNuts: Unit = {
      println("AntiDemo")
      time[Long]("scalar sum") { (1L to 10000000L).map(_ * 2L).sum }
      time[Long]("parallel sum") { (1L to 10000000L).par.map(_ * 2L).sum }
      println()
    }
  }

  new AntiDemo().goNuts

  val cpuBound = new CpuBound()
  val result = cpuBound.goNuts(1000)

  println()  // Work towards reducing results
  println("Class name of results: " + result.getClass.getName)
  println("Reduced value: " + result.filter(_.toString.contains("6")).reduce { (acc, n) ⇒ acc + n })
  println("Summed value: " + result.filter(_.toString.contains("6")).sum)
  println()

  println(s"${cpuBound.parSixes} of the ${cpuBound.iterations} results had sixes in them.")
  println()

  new IoBound().goNuts
}
