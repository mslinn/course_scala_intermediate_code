object TimedTask {
  import java.util.Timer
  import java.util.TimerTask

  def apply(intervalSeconds: Int=1)(op: => Unit) {
    val task = new TimerTask {
      def run = op
    }
    val timer = new Timer
    timer.schedule(task, 0L, intervalSeconds*1000L)
  }
}

object HigherFun extends App {
  var i = 0

  TimedTask(1) {
    println(s"Tick #$i")
    i = i + 1
  }
}
