package object multi {
  lazy val goodUrlStr1       = "http://www.scalacourses.com"
  lazy val goodUrlStr2       = "http://www.micronauticsresearch.com"
  lazy val badHostUrlStr     = "http://www.not-really-here.com"
  lazy val badPageUrlStr     = "http://scalacourses.com/noPageHere"
  lazy val badProtocolUrlStr = "blah://scalacourses.com"

  val random = new util.Random()

  val calculatePiFor: Int => Double = (decimals: Int) => {
    var acc = 0.0
    for (i <- 0 until decimals)
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  def factorial(number: BigInt): BigInt = {
    @annotation.tailrec
    def fact(total: BigInt, number: BigInt): BigInt = {
      if (number == BigInt(1))
        total
      else
        fact(total* number, number - 1)
    }
    fact(1, number)
  }

  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

  /** @return up to first maxChars characters of web page at given url */
  def readUrl(url: String, maxChars: Int=500): String = {
    val contents = io.Source.fromURL(url).mkString.trim
    contents.substring(0, math.min(contents.length, maxChars)) + (if (contents.length>maxChars) "..." else "")
  }

  /** Measure execution time of the given block of code */
  def time[T](msg: String)(block: => T): T = {
    val t0 = System.nanoTime
    val result: T = block
    val elapsedMs = (System.nanoTime - t0) / 1000000
    println(s"  Elapsed time for $msg: " + elapsedMs + "ms")
    result
  }

  def urls(includeBad: Boolean=false): List[String] =
    List(goodUrlStr1, goodUrlStr2) :::
      (if (includeBad) List(badHostUrlStr, badPageUrlStr, badProtocolUrlStr) else Nil)
}
