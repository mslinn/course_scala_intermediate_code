object Adder extends App {
  def getInt(str: String): Int =
    try { str.toInt } catch { case _: Throwable => 0 }

  var total = 0
  1 to 3 foreach { _ =>
    val input = io.StdIn.readLine("I need a number: ")
    val int = getInt(input)
    total = total + int
    println(s"Total: $total")
  }
}
