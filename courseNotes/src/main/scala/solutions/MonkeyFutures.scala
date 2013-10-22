package solutions

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.parallel._

trait Timeable[T] {
  def time(block: => T): T = {
    val t0 = System.nanoTime
    val result: T = block
    val elapsedMs = (System.nanoTime - t0) / 1000000
    println("Elapsed time: " + elapsedMs + "ms")
    result
  }
}

/** Solution to http://www.scalacourses.com/lectures/admin/showLecture/17/90
  * This program uses more memory as the number of iterations increases.
  * For 13000000L iterations, specify -Xmx2g.
  * For that many iterations, you should only expect 3 characters to match in about a minute on a fast machine.
  * This naive implementation' computational effort increases geometrically with the number of iterations. */
object FutureMonkey extends App with Timeable[Future[String]]{
  val iterations: Long = 13000000L
  val singleFutureTimeout = Duration(20, "seconds")

  val stringToMatch = "I thought I saw a lolcat! I did, I did see a lolcat!"
  val random = new scala.util.Random
  val allowableChars = """ !.,;'""" + (('a' to 'z').toList ::: ('A' to 'Z').toList
    ::: (0 to 9).toList).mkString

  try {
    val result = Await.result(time(parFun), singleFutureTimeout)
    println(s"Best match is: '$result'")
  } catch {
    case exception: Throwable =>
      println(exception.getMessage)
  }

  /** Generate a random string of length n from the given alphabet */
  def randomString(alphabet: String, n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

  def checkIt: Future[String] = Future {
    val item = randomString(allowableChars, stringToMatch.length)
    matchSubstring(item, stringToMatch)
  }

  def parFun: Future[String] = {
    print(s"Mapping $iterations iterations")
    val strs: ParSeq[Future[String]] = for {
      i <- (1L to iterations).toList.par
    } yield {
      if (i % (iterations/100L)==0) print(".") // print one dot for each % complete
      checkIt
    }
    println("\nReducing in parallel...")
    strs.reduce((acc, item) => if (acc.value.mkString.length < item.value.mkString.length) item else acc)
  }
}
