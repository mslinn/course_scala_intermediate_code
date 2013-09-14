import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

object FutureFun extends App {
  val urls2 = List("http://scalacourses.com", "http://not_really_here.com", "http://micronauticsresearch.com")

  def readUrl(url: String): String = Source.fromURL(url).mkString

  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map { url =>
      Future(readUrl(url)).recoverWith {
        case e: Exception => Future.successful("")
      }
    }
    val sequence: Future[List[String]] = Future.sequence(futures2)
    Await.ready(sequence, 30 seconds) // block until all futures complete, timeout occurs, or a future fails
    println("sequence completed: " + sequence.isCompleted) // false if timeout occurred
    for {
      (url, future) <- urls2 zip futures2
      contents <- future if contents.toLowerCase.contains(word)
    } println(s"'$word' was found in $url")
  }

  urlSearch("scala", urls2)
}
