import java.io.{BufferedReader, FileInputStream, FileReader, InputStreamReader}

trait MaybeMaybeNot {
  def square(num: Int): Int = num * num

  val maybeInt = Some(3)
  println(s"maybeInt.map(x => square(x)) = ${maybeInt.map(x => square(x))}")

  println(s"maybeInt.map(x => x.toDouble) = ${maybeInt.map(x => x.toDouble)}")
  Array(1, 2, 3).map(x => x.toDouble)
}

object HigherFun extends App with MaybeMaybeNot

object HigherShorthand extends App with MaybeMaybeNot{
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
      def run(): Unit = op
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

object BinaryIO extends App with AutoCloseableLike {
  import scala.language.postfixOps

  val scalaCompilerPath: String = {
    import sys.process._
    "which scalac".!!.trim
  }

  // read binary file the right way:
  val text1: String = using(
    new BufferedReader(new InputStreamReader(new FileInputStream(scalaCompilerPath), "ISO-8859-1"))
  ) {
    _.readLine
  }.mkString("\n")

  // read binary file the wrong way (leaves file open):
  val text2 = scala.io.Source.fromFile(scalaCompilerPath, "ISO-8859-1")

  // This is another way to read a file, but it should be wrapped in try/finally; can you do it?
  val bis = new java.io.BufferedInputStream(new java.io.FileInputStream(scalaCompilerPath))
  val compilerAsByteArray3 = LazyList.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
  bis.close()
}
