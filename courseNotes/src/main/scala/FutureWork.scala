import scala.concurrent._
import scala.util._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object FutureWork extends App {
  import FutureUtilities.snippet

  val urls = List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com")
  val futures: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))

  def urlSearch(word: String, urls: List[String]): List[Future[String]] = {
    val futures: List[Future[String]] = urls.map(u => Future(io.Source.fromURL(u).mkString))
    for {
      (url, future) <- urls zip futures
      contents <- future if contents.toLowerCase.contains(word)
    } println(s"urlSearch: $url contains '$word'")
    futures
  }

  Await.ready(Future.sequence(urlSearch("free", urls)), Duration.Inf)


  def slowUrlSearch(word: String, urls: List[String]): Unit = {
    for {
      url <- urls
      contents <- Future(io.Source.fromURL(url).mkString)
      if contents.toLowerCase.contains(word)
    } println(s"slowUrlSearch: $url contains '$word'")
  }

  slowUrlSearch("free", urls)


  val containsFree: (String, List[Future[String]]) => Future[List[String]] =
    (word: String, futures: List[Future[String]]) =>
      Future.sequence(futures).collect {
        case list: List[String] =>
          val resultList = list.filter(_.toLowerCase.contains("free"))
          resultList.foreach { contents => println(s"containsFree: ${snippet("free", contents)}") }
          resultList
      }.andThen{
        case Success(list) =>
          println("containsFree succeeded!")

        case Failure(ex) =>
          println("containsFree failed on URL " + ex.getMessage)
      }

  Await.ready(containsFree("free", futures), Duration.Inf)
}

object FutureUtilities {
  /** @return String of the form [...]blah blah word blah blah [...] */
  def snippet(word: String, string: String): String = {
    val m = string.trim.toLowerCase
    val i = math.max(0, m.indexOf(word) - 50)
    val j = math.min(m.length, i + 100)
    val result = (if (i == 0) "" else "...") + m.substring(i, j).trim + (if (j == m.length) "" else "...")
    result
  }
}

object FutureMixed extends App {
  import scala.concurrent._
  import FutureUtilities.snippet

  // Error:(100, 16) type mismatch;
  // found   : scala.concurrent.Future[String]
  // required: scala.collection.GenTraversableOnce[?]
  //      contents <- future if contents.toLowerCase.contains(word)
  //               ^
  //def urlSearch(word: String, urls: List[String]) = {
  //  val futures: List[Future[String]] = urls.map(u => Future(io.Source.fromURL(u).mkString))
  //  for {
  //    (url, future) <- urls zip futures
  //    contents <- future if contents.toLowerCase.contains(word)
  //  } yield url
  //}

  val urls = List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com")
  val indices = List(1, 2, 3)
  val result1 = for (url <- urls; index <- indices) yield (index, url)
  println(s"for-comprehension result = $result1")

  val result2 = urls.flatMap { url => indices.map { index => (index, url) } }
  println(s"flatMap/map result = $result2")

  val futureContent: String => (String, Future[String]) = (url: String) => (url, Future(io.Source.fromURL(url).mkString))
  val futureContents: List[String] => List[(String, Future[String])] =
    (_: List[String]).collect { case url => (url, Future(io.Source.fromURL(url).mkString)) }

  val result3 = futureContents(urls).map { case (url, future) => (future, url) }
  println(s"map/map result = $result3")

  /** @return value of completed Future, or empty string if any Exception */
  val futureString: Future[String] => String = (future: Future[String]) =>
    try {
      Await.result(future, duration.Duration.Inf)
    } catch {
      case e: Exception => ""
    }

  val listOfTuples: List[(String, String)] =
    futureContents(urls).collect {
      case (url, future) if futureString(future).contains("free") =>
        //println(s"$url contains 'free'")
        (snippet("free", futureString(future)), url)
    }
  println(s"listOfTuples = $listOfTuples")
}

object FutureSelect extends App {
  import FuturesUtil.asapFutures
  import FutureUtilities.snippet

  def urlSearch6(word: String, urls: List[String])(whenDone: =>Unit={}): Unit = {
    val futures = urls.map(url ⇒ Future((url, io.Source.fromURL(url).mkString)))
    asapFutures(futures) {
      case Success((url, contents)) if contents.toLowerCase.contains(word) =>
        println(s"Found '$word' in $url:\n${snippet(word, contents)}\n")

      case Success((url, contents)) =>
        println(s"Sorry, $url does not contain '$word'\n")

      case Failure(err) =>
        println(s"Error: Could not read from ${err.getMessage}\n")
    }(whenDone)
  }

  val urls = List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com")
  val signal1 = Promise[String]()
  urlSearch6("free", urls) { signal1.success("done") }
  Await.ready(signal1.future, duration.Duration.Inf)

  val signal2 = Promise[String]()
  urlSearch6("free", Nil) { signal2.success("done") }
  Await.ready(signal2.future, duration.Duration.Inf)

  urlSearch6("free", Nil)()
  println("All done")
}

object FutureCancel extends App {
  import scala.concurrent._
  import scala.util.{Failure, Success}
  import FuturesUtil.interruptableFuture

  val signal = Promise[String]()
  val (future, cancel) = interruptableFuture(io.Source.fromURL("http://scalacourses.com").mkString)
  val wasCancelled = cancel("Die!")
  cancel("Die again!")
  println(s"Fetch of ScalaCourses.com wasCancelled: ${wasCancelled.isDefined}\n")

  // List of slow web sites: http://internetsupervision.com/scripts/urlcheck/report.aspx?reportid=slowest
  val urls = List("http://magarihub.com", "http://vitarak.com", "http://www.firstpersonmedical.com")
  val iFutures = urls.map(u ⇒ interruptableFuture((u, io.Source.fromURL(u).mkString)))

  Future.firstCompletedOf(iFutures.map(_._1)).andThen { // cancel all the remaining futures
    case _ =>
      iFutures.foreach {
        case iFuture if !iFuture._1.isCompleted =>
          val i = iFutures.indexOf(iFuture)
          iFuture._2(s"Cancelled fetch of ${urls(i)}")

        case completedIFuture =>
      }
      signal.complete(Success("All done"))
  }

  iFutures foreach { case (iFuture, cancelThis) =>
    iFuture.onComplete {
      case Success((url, contents)) =>
        println(s"Fetched $url successfully")

      case Failure(throwable) => println(throwable)
    }
  }

  Await.ready(signal.future, duration.Duration.Inf)
}
