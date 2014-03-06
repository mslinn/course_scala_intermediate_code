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
