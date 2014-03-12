package solutions

import java.util.Calendar

object Loopy extends App {
  val xmas = Calendar.getInstance()
  xmas.set(Calendar.MONTH, Calendar.DECEMBER)
  xmas.set(Calendar.DAY_OF_MONTH, 25)

  def secsUntilXmas: Long = (xmas.getTimeInMillis - System.currentTimeMillis) / 1000

  1 to 3 foreach { i =>
    println(s"Only $secsUntilXmas seconds until Christmas!")
    Thread.sleep(5000)
  }
}
