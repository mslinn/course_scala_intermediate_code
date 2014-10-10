import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

object FutureArtifacts {
  val urls = List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com")

  val futureContents: List[String] => List[Future[String]] =
    (_: List[String]).map { url => Future(io.Source.fromURL(url).mkString) }

  val futureTuples: List[String] => List[Future[(String, String)]] =
    (_: List[String]).map { url => Future((url, io.Source.fromURL(url).mkString)) }

  /** @return String of the form [...]blah blah word blah blah [...] */
  def snippet(word: String, string: String): String = {
    val m = string.trim.toLowerCase
    val i = math.max(0, m.indexOf(word) - 50)
    val j = math.min(m.length, i + 100)
    val result = (if (i == 0) "" else "...") + m.substring(i, j).trim + (if (j == m.length) "" else "...")
    result
  }
}

object FutureWork extends App {
  import FutureArtifacts._

  val futuresTemp: List[Future[String]] = urls.map(url => Future(io.Source.fromURL(url).mkString))
  println(s"futuresTemp=$futuresTemp")

  def urlSearch(word: String, urls: List[String]): List[Future[String]] = {
    val futures: List[Future[String]] = futureContents(urls)
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


  val brokenUrlSearch: (String, List[String]) => Future[List[String]] =
    (word: String, urls: List[String]) =>
      Future.sequence(futureContents(urls)).collect {
        case list: List[String] =>
          val resultList = list.filter(_.toLowerCase.contains(word))
          resultList.foreach { contents => println(s"containsFree: ${snippet(word, contents)}") }
          resultList
      }.andThen{
        case Success(list) =>
          println("containsFree succeeded!")

        case Failure(ex) =>
          println("containsFree failed on URL " + ex.getMessage)
      }

  Await.ready(brokenUrlSearch("free", urls), Duration.Inf)
}

object FutureSelect extends App {
  import FutureArtifacts._
  import FuturesUtil.asapFutures

  def urlSearch2(word: String, urls: List[String])(whenDone: =>Unit={}): Unit = {
    asapFutures(futureTuples(urls)) {
      case Success((url, contents)) if contents.toLowerCase.contains(word) =>
        println(s"Found '$word' in $url:\n${snippet(word, contents)}\n")

      case Success((url, contents)) =>
        println(s"Sorry, $url does not contain '$word'\n")

      case Failure(err) =>
        println(s"Error: ${err.getMessage}\n")
    }(whenDone)
  }

  val signal1 = Promise[String]()
  urlSearch2("free", urls) { signal1.success("done") }
  Await.ready(signal1.future, duration.Duration.Inf)

  val signal2 = Promise[String]()
  urlSearch2("free", Nil) { signal2.success("done") }
  Await.ready(signal2.future, duration.Duration.Inf)

  urlSearch2("free", Nil)()
  println("All done")
}

object FutureMixed extends App {
  import scala.concurrent._
  import FutureArtifacts._

  //def urlSearch3(word: String, urls: List[String]) = {
  //  val futures: List[Future[String]] = urls.map(u => Future(io.Source.fromURL(u).mkString))
  //  for {
  //    (url, future) <- urls zip futures
  //    contents <- future if contents.toLowerCase.contains(word)
  //  } yield url
  //}
  // Error:(100, 16) type mismatch;
  // found   : scala.concurrent.Future[String]
  // required: scala.collection.GenTraversableOnce[?]
  //      contents <- future if contents.toLowerCase.contains(word)
  //               ^

  val indices = List(1, 2, 3)
  val result1 = for (url <- urls; index <- indices) yield (index, url)
  println(s"for-comprehension result = $result1")

  val result2 = urls.flatMap { url => indices.map { index => (url, index) } }
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

  val listOfTuples: String => List[(String, String)] = (word: String) =>
    futureContents(urls).collect {
      case (url, future) if futureString(future).contains(word) =>
        //println(s"$url contains '$word'")
        (snippet(word, futureString(future)), url)
    }
  println(s"listOfTuples = ${listOfTuples("free")}")
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
