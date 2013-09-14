import concurrent.{ExecutionContext, Await, Future}
import concurrent.duration._
import io.Source
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.forkjoin.ForkJoinPool

// WARNING: if you use concurrent.ExecutionContext.Implicits.global, daemon threads are used
// once the program has reached the end of the main program, any other threads still executing
// are terminated
//import concurrent.ExecutionContext.Implicits.global

object FutureFun extends App {
  val pool: ExecutorService = new ForkJoinPool// Executors.newFixedThreadPool(8)
  // If you have Java 7+ you should use:
  //val pool: ExecutorService = new java.util.concurrent.ForkJoinPool()
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(pool)

  val urls2 = List("http://www.scalacourses.com", "http://www.not_really_here.com", "http://www.micronauticsresearch.com")

  def readUrl(url: String): String = Source.fromURL(url).mkString

  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map { url =>
      Future(readUrl(url)).recover {
        case e: Exception =>
          println(s"Handling URL read exception on $url")
          ""
      }
    }
    val sequence: Future[List[String]] = Future.sequence(futures2)
    Await.ready(sequence, 30 seconds) // block until all futures complete, timeout occurs, or a future fails
    println(s"""sequence ${if (sequence.isCompleted) "succeeded within timeout period" else "timed out"}""")
    for {
      (url, future) <- urls2 zip futures2
      contents: String <- future
    } {
      println(s"Scanning $url (${contents.length} characters)")
      if (contents.toLowerCase.contains(word)) {
        println(s"  $word was found in $url")
      } else {
        println(s"  $url did not contain $word")
      }
    }
    println()
  }

  urlSearch("scala", urls2)
}
