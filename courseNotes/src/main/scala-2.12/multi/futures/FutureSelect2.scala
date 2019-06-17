import multi.readUrl
import scala.concurrent.{Await, Promise, duration}
import scala.util.{Failure, Success}

object FutureSelect extends App {
  import multi.futures.FutureArtifacts._
  import multi.futures.FuturesUtil.asapFutures

  def urlSearch(word: String, urls: List[String])(whenDone: =>Unit={}): Unit = {
    asapFutures(futureTuples(urls)) {
      case Success((_, _)) if contents.toLowerCase.contains(word) =>
        println(s"Found '$word' in $url:\n${snippet(word, contents)}\n")

      case Success((_, _)) =>
        println(s"Sorry, $url does not contain '$word'\n")

      case Failure(_) =>
        println(s"Error: ${err.getMessage}\n")
    }(whenDone)
  }

  val signal1 = Promise[String]()
  urlSearch("free", urls()) { signal1.success("done") }
  Await.ready(signal1.future, duration.Duration.Inf)

  val signal2 = Promise[String]()
  urlSearch("free", Nil) { signal2.success("done") }
  Await.ready(signal2.future, duration.Duration.Inf)

  urlSearch("free", Nil)()
  println("All done")
}

object FutureCancel1 extends App {
  import multi.futures.FuturesUtil.interruptibleFuture

  val (future, cancel) = interruptibleFuture(readUrl("http://scalacourses.com"))
  val wasCancelled = cancel("Die!")
  cancel("Die again!")
  println(s"Fetch of ScalaCourses.com wasCancelled: ${wasCancelled.isDefined}\n")
}

object FutureCancel2 extends App {
  import multi.futures.FuturesUtil.interruptibleFuture
  import scala.concurrent._

  // List of slow web sites: http://internetsupervision.com/scripts/urlcheck/report.aspx?reportid=slowest
  val urls = List("http://magarihub.com", "http://vitarak.com", "http://www.firstpersonmedical.com")
  val iFutures: List[(Future[(String, String)], (String) => Option[CancellationException])] =
    urls.map(url => interruptibleFuture((url, readUrl(url))))

  val signal = Promise[String]()
  Future.firstCompletedOf(iFutures.map(_._1)).andThen { // cancel all the remaining futures
    case _ =>
      iFutures.foreach {
        case iFuture if !iFuture._1.isCompleted =>
          val i = iFutures.indexOf(iFuture)
          iFuture._2(s"Cancelled fetch of ${urls(i)}")
          println(s"${ iFuture._1.value.get.failed.get.getMessage }")

        case iFuture =>
          val i = iFutures.indexOf(iFuture)
          println(s"Fetched ${ urls(i) } successfully")
      }
      signal.complete(Success("All done"))
  }

  Await.ready(signal.future, duration.Duration.Inf)
}
