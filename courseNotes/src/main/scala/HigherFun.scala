object HigherFun extends App {
  def square(num: Int): Int = num * num

  val maybeInt = Some(3)
  println(s"maybeInt.map(x => square(x)) = ${maybeInt.map(x => square(x))}")

  println(s"maybeInt.map(x => x.toDouble) = ${maybeInt.map(x => x.toDouble)}")
  Array(1, 2, 3).map(x => x.toDouble)
}

object HigherShorthand extends App {
  import HigherFun._

  maybeInt.map(_.toDouble)
  maybeInt.map(square _)
  maybeInt.map(square)
  maybeInt map square

  maybeInt.map(x => x * x)
}

object TimedTask {
  import java.util.{Timer, TimerTask}

  def apply(intervalSeconds: Int=1)(op: => Unit) {
    val task = new TimerTask {
      def run() = op
    }
    val timer = new Timer
    timer.schedule(task, 0L, intervalSeconds*1000L)
  }
}

object TimedFun extends App {
  var i = 0

  TimedTask(1) {
    println(s"Tick #$i")
    i = i + 1
  }
}

object TimedPi extends App {
  def time(block: => Any): Any = {
    val t0 = System.nanoTime()
    val result: Any = block
    val elapsedMs = (System.nanoTime() - t0) / 1000000
    println("Elapsed time: " + elapsedMs + "ms")
    result
  }

  def calculatePiFor(decimalPlaces: Int): Double = {
    var acc = 0.0
    for (i <- 0 until decimalPlaces)
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  println(time(calculatePiFor(100000)))
}

object BinaryIO extends App {
  val scalaCompilerPath: String = {
    import sys.process._
    ("which scalac" !!).trim
  }

  val compilerObject1 = io.Source.fromFile(scalaCompilerPath, "ISO-8859-1").map(_.toByte).toArray

  val compilerObject2 = scala.io.Source.fromFile(scalaCompilerPath, "ISO-8859-1")
  val compilerAsByteArray = compilerObject2.map(_.toByte).toArray
  compilerObject2.close()

  val compilerAsByteArray2 = try { compilerObject2.map(_.toByte).toArray } finally { compilerObject2.close() }

  val bis = new java.io.BufferedInputStream(new java.io.FileInputStream(scalaCompilerPath))
  val compilerAsByteArray3 = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
  bis.close()
}
