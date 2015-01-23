package multi.futures

import multi._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util._

object FutureArtifacts {
  val readUrl: String => String = io.Source.fromURL(_: String).mkString

  val futureContents: List[String] => List[Future[String]] =
    (_: List[String]).map { url => Future(readUrl(url)) }

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

object FutureWord extends App {
  import multi.futures.FutureArtifacts._

  def urlSearch(word: String, urls: List[String]): List[Future[String]] = {
    val futures = futureContents(urls)
    for {
      (url, future) <- urls zip futures
      contents      <- future if contents.toLowerCase.contains(word)
    } println(s"$url contains '$word'")
    futures // program can terminate gracefully because Future.sequence waits for all futures to complete
  }

  Await.ready(Future.sequence(urlSearch("scala", urls(includeBad=true))), Duration.Inf)
}

object FutureLoopDetail extends App {
  import java.util.concurrent.atomic.AtomicInteger

  def report(url: => String, i: Int): Future[(String, Int)] = Future {
    println(s"$i + Starting $url future")
    (readUrl(url), i)
  }

  def urlSearch(word: String, urls: List[String]): Unit = {
    val count = new AtomicInteger(urls.size-1)
    for {
      (url, i)      <- urls.zipWithIndex
      (contents, j) <- report(url, i).andThen { case Failure(e) =>
                                                  println(s"$i - ${e.getClass.getName}: ${e.getMessage}")
                                                  if (count.getAndDecrement==0) System.exit(0)
                                              }
    } {
      if (contents.toLowerCase.contains(word))
        println(s"$i - $url DOES contain '$word'")
      else
        if (contents.nonEmpty) println(s"$i - $url does NOT contain '$word'")
      if (count.getAndDecrement==0) System.exit(0)
    }
  }
  urlSearch("scala", urls(includeBad=true))
  synchronized { wait() } // there is no good way to terminate this program so it hangs
}

object FutureAsync extends App {
  import multi.futures.FutureArtifacts._
  import scala.collection.immutable.ListMap

  object ExternalGateway {
    def asyncReadUrl(url: => String): Future[String] = Future(readUrl(url))
  }

  object LexicalAnalyserService {
    def asyncWordCount(string: => String): Future[ListMap[String, Int]] = Future {
      val rawWordCount: List[(String, Int)] = string.split(" ")
                                            .foldLeft(ListMap.empty[String, Int] withDefaultValue 0) {
                                              (m, x) => m + (x -> (1 + m(x)))
                                            }.toList
      val sortedWordCount = ListMap.empty[String, Int] ++ rawWordCount.sortBy(_._2).reverse
      sortedWordCount.filterNot { case (key, value) => commonWords.contains(key) }
    }
  }

  object SecurityProxy {
    def asyncRemoveHtml(string: => String): Future[String] =
      Future(string.replaceAll("[\\s]+", " ")
                   .replaceAll("<style>.*?</style>", "")
                   .replaceAll("<script>.*?</script>", "")
                   .replaceAll("<(.*?)>", "")
                   .replaceAll("&.*?;", "")
                   .replaceAll("[^\\p{L} ]", " ")
                   .replaceAll("[\\s]+", " ")
                   .trim)
  }

  val commonWords = List("the", "to", "and", "of", "in", "a", "is", "are", "with", "for", "on", "at")

  def mostCommonWords(url: => String, n: => Int): Future[ListMap[String, Int]] =
    for {
      html       <- ExternalGateway.asyncReadUrl(url)
      contents   <- SecurityProxy.asyncRemoveHtml(html)
      wordCounts <- LexicalAnalyserService.asyncWordCount(contents.toLowerCase)
    } yield wordCounts.take(n)

  val n = 10
  val done = mostCommonWords(goodUrlStr1, n) andThen {
    case Success(value) => println(value.mkString("\n"))
    case Failure(ex)    => println(ex.getMessage)
  }
  val result: ListMap[String, Int] = Await.result(done, Duration.Inf)
  println(s"The $n most common words in $goodUrlStr1 are:\n${result.mkString("\n")}")
}

object FutureFailed extends App {
  import FutureArtifacts._

  class FailureTest {
    val brokenUrlSearch: (String, List[String]) => Future[List[String]] =
      (word: String, urls: List[String]) =>
        Future.sequence(futureContents(urls)).collect {
          case list: List[String] =>
            val resultList = list.filter(_.toLowerCase.contains(word))
            resultList.foreach { contents => println(snippet(word, contents)) }
            resultList
        } andThen {
          case Success(list) =>
            println("Succeeded")

          case Failure(ex) =>
            println("Failed on URL " + ex.getMessage)
        }
  }

  Await.ready(new FailureTest().brokenUrlSearch("scala", urls(includeBad=true)), Duration.Inf)
}

object FutureRecovering extends App {
  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures: List[Future[String]] = urls.map { url ⇒
      Future(readUrl(url)).recoverWith {
        case e: Exception ⇒ Future.successful("") // catches all Exceptions
      }
    }

    val sequence: Future[List[String]] = Future.sequence(futures)
    concurrent.Await.ready(sequence, 30 seconds) // block until all futures complete or timeout occurs
    println("sequence completed: " + sequence.isCompleted) // false if timeout occurred

    for {
      (url, future) <- urls zip futures
      contents      <- future if contents.toLowerCase.contains(word)
    } println(s"'$word' was found in $url")
  }

  urlSearch("scala", urls(includeBad=true))
  synchronized { wait() } // program hangs, not a very good design
}

object FutureSelect extends App {
  import multi.futures.FutureArtifacts._
  import multi.futures.FuturesUtil.asapFutures

  def urlSearch(word: String, urls: List[String])(whenDone: =>Unit={}): Unit = {
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
  urlSearch("free", urls()) { signal1.success("done") }
  Await.ready(signal1.future, duration.Duration.Inf)

  val signal2 = Promise[String]()
  urlSearch("free", Nil) { signal2.success("done") }
  Await.ready(signal2.future, duration.Duration.Inf)

  urlSearch("free", Nil)()
  println("All done")
}

object FutureMixed extends App {
  import multi.futures.FutureArtifacts._

  //def urlSearch(word: String, urls: List[String]) = {
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

  val indices = (1 to 3).toList
  val result1 = for (url <- urls(); index <- indices) yield (index, url)
  println(s"for-comprehension result = $result1")

  val result2 = urls().flatMap { url => indices.map { index => (url, index) } }
  println(s"flatMap/map result = $result2")

  val futureContent: String => (String, Future[String]) = (url: String) => (url, Future(io.Source.fromURL(url).mkString))
  val futureContents: List[String] => List[(String, Future[String])] =
    (_: List[String]).collect { case url => (url, Future(io.Source.fromURL(url).mkString)) }

  val result3 = futureContents(urls()).map { case (url, future) => (future, url) }
  println(s"map/map result = $result3")

  /** @return value of completed Future, or empty string if any Exception */
  val futureString: Future[String] => String = (future: Future[String]) =>
    try {
      Await.result(future, duration.Duration.Inf)
    } catch {
      case e: Exception => ""
    }

  val listOfTuples: String => List[(String, String)] = (word: String) =>
    futureContents(urls()).collect {
      case (url, future) if futureString(future).contains(word) =>
        //println(s"$url contains '$word'")
        (snippet(word, futureString(future)), url)
    }
  println(s"listOfTuples = ${listOfTuples("free")}")
}

object FutureCancel extends App {
  import multi.futures.FuturesUtil.interruptableFuture
  import scala.concurrent._

  val signal = Promise[String]()
  val (future, cancel) = interruptableFuture(readUrl("http://scalacourses.com"))
  val wasCancelled = cancel("Die!")
  cancel("Die again!")
  println(s"Fetch of ScalaCourses.com wasCancelled: ${wasCancelled.isDefined}\n")

  // List of slow web sites: http://internetsupervision.com/scripts/urlcheck/report.aspx?reportid=slowest
  val urls = List("http://magarihub.com", "http://vitarak.com", "http://www.firstpersonmedical.com")
  val iFutures = urls.map(url => interruptableFuture((url, readUrl(url))))

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
