def time[T](block: => T): T = {
  val t0 = System.nanoTime()
  val result: T = block
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
time(calculatePiFor(100000))
time[Double](calculatePiFor(100000))
import io.Source
time(Source.fromURL("http://scalacourses.com").trim)
case class Thing(val i: Int, val s: String) extends Ordered[Thing] {
  def compare(that: Thing) =  {
    val primaryKey = this.i - that.i
    if (primaryKey!=0) primaryKey else this.s compare that.s
  }

  override def equals(other: Any) = {
    val that = other.asInstanceOf[Thing]
    this.i == that.i && this.s==that.s
  }

  override def hashCode = super.hashCode
}
val thing1 = Thing(1, "z")
val thing2 = Thing(2, "y")
val thing3 = Thing(3, "x")
thing1>thing2
thing1<thing2
thing1 == Thing(1, "z")
thing1==thing2
thing1<=thing2
thing1>=thing2
import scala.util.Sorting.quickSort
val things: Array[Thing] = Array(thing2, thing1, thing3)
quickSort(things)
things

