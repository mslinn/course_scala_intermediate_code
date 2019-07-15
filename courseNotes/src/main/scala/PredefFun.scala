object PredefFun extends App {
  def percentImprovement(a: Int, b: Int): Double = {
    require(b!=0)  // Prevent divide by zero exception
    (b.toDouble - a.toDouble) / a.toDouble * 100.0
  }

  val percent1 = percentImprovement(40, 50)
  println(f"Percent increase from 40 to 50 Is $percent1%.2f%%")

// val percent2 = percentImprovement(40, 0)
//  java.lang.IllegalArgumentException: requirement failed
//    at scala.Predef$.require(Predef.scala:327)
//    at .percentImprovement(<console>:2)
//    ... 28 elided

  def factorial(i: Int): Long = {
    import scala.annotation.tailrec

    require(i >= 0, "i must be non-negative") // this is for correct input

    @tailrec def loop(k: Int, result: Long = 1): Long = {
      assert(result == 1 || result >= k)   // this is only for verification

      if (k > 0) loop(k - 1, result * k) else result
    }

    loop(i)
  }
}
