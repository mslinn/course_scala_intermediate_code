import concurrent.Future
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global

object FutureWork extends App {
  val urls = List("http://scalacourses.com", "http://micronauticsresearch.com", "http://mslinn.com")
  val futures = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))

  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures: List[Future[String]] = urls.map(u => Future(io.Source.fromURL(u).mkString))
    for {
      (url, future) <- urls zip futures
      contents <- future if contents.toLowerCase.contains(word)
    } println(s"urlSearch: $url contains '$word'")
  }

  urlSearch("scala", List("http://micronauticsresearch.com"))
  urlSearch("free", List("http://scalacourses.com"))
  urlSearch("free", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

  def slowUrlSearch(word: String, urls: List[String]): Unit = {
    for {
      url <- urls
      contents <- Future(io.Source.fromURL(url).mkString)
      if contents.toLowerCase.contains(word)
    } println(s"slowUrlSearch: $url contains '$word'")
  }

  slowUrlSearch("free", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

  val containsFree: Future[List[String]] = Future.sequence(futures).collect {
    case value: List[String] ⇒ value.filter(_.toLowerCase.contains("free"))
  }

  containsFree.onComplete {
    case scala.util.Success(value) => println(value)
  }

  val urls2 = List("http://scalacourses.com", "http://not_really_here.com", "http://micronauticsresearch.com")
  val futures2 = urls2.map(u ⇒ Future(io.Source.fromURL(u).mkString))

  def urlSearch2(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    for {
      allDone ← Future.sequence(futures2)
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } {
      println(s"urlSearch2: $url contains '$word'")
    }
  }

  urlSearch2("free", urls2)
  urlSearch2("scala", urls2)

  def urlSearch3(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    concurrent.Await.ready(Future.sequence(futures2), Duration.Inf) // block until all futures complete
    for {
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } {
      println(s"urlSearch3: $url contains '$word'")
    }
  }

  urlSearch3("free", urls2)
  urlSearch3("scala", urls2)

  def readUrl4(url: String): String = io.Source.fromURL(url).mkString

  def urlSearch4(word: String, urls: List[String]): Unit = {
      val futures2: List[Future[String]] = urls.map { url ⇒
        Future(readUrl4(url)).recoverWith {
          case e: Exception ⇒ Future.successful("") // catches all Exceptions
        }
      }
      val sequence: Future[List[String]] = Future.sequence(futures2)
      concurrent.Await.ready(sequence, 30 seconds) // block until all futures complete, timeout occurs, or a future fails
      println("sequence completed: " + sequence.isCompleted) // false if timeout occurred
      for {
        (url, future) ← urls zip futures2
        contents ← future if contents.toLowerCase.contains(word)
      } println(s"urlSearch4: '$word' was found in $url")
    }

  def urlSearch5(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    for {
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } println(s"urlSearch5: $url contains '$word'")
  }
}

object FutureSelect extends App {
  import concurrent._
  import scala.util._
  import scala.annotation.tailrec

  /** @return the first future to complete, with the remainder of the Futures as a sequence.
   * @param futures a scala.collection.Seq
   * @author Victor Klang (https://gist.github.com/4488970)
   * @author Mike Slinn */
  def select[A](futures: Seq[Future[A]])(implicit ec: ExecutionContext): Future[(Try[A], Seq[Future[A]])] = {
    @tailrec
    def stripe(promise: Promise[(Try[A], Seq[Future[A]])],
               head: Seq[Future[A]],
               thisFuture: Future[A],
               tail: Seq[Future[A]]): Future[(Try[A], Seq[Future[A]])] = {
      thisFuture onComplete { result => if (!promise.isCompleted) promise.trySuccess((result, head ++ tail)) }
      if (tail.isEmpty) promise.future
      else stripe(promise, head :+ thisFuture, tail.head, tail.tail)
    }

    if (futures.isEmpty) Future.failed(new IllegalArgumentException("List of futures is empty"))
    else stripe(Promise(), futures.genericBuilder[Future[A]].result(), futures.head, futures.tail)
  }


  /** Apply a function over a sequence of Future as soon as each future completes.
    * @param futures sequence of Future to operate on
    * @param operation Function1 to apply on each Future value as soon as the Future completes
    * @param whenDone block of code to execute when all futures have been processed
    * @author David Crosson
    * @author Mike Slinn */
  def asapFutures[T](futures: Seq[Future[T]])(operation: Try[T]=>Unit)(whenDone: =>Unit): Unit = {
    def jiffyFutures(futures: Seq[Future[T]])(operation: Try[T]=>Unit)(whenDone: =>Unit): Unit = {
      if (futures.nonEmpty) {
        select(futures) andThen {
          case Success((tryResult, remainingFutures)) =>
            operation(tryResult)
            jiffyFutures(remainingFutures)(operation)(whenDone)

          case Failure(throwable) =>
            println("Unexpected exception: " + throwable.getMessage)
        } andThen {
          case _ =>
            if (futures.size==1)
              whenDone
        }
      }
    }

    if (futures.isEmpty)
      whenDone
    else
      jiffyFutures(futures)(operation)(whenDone)
  }

  def urlSearch6(word: String, urls: List[String])(whenDone: =>Unit): Unit = {
    val futures = urls.map(u ⇒ Future((u, io.Source.fromURL(u).mkString)))
    asapFutures(futures) {
      case Success(tuple) if tuple._2.toLowerCase.contains(word) =>
        val m = tuple._2.trim.toLowerCase
        val i = math.max(0, m.indexOf(word) - 50)
        val j = math.min(m.length, i + 100)
        val snippet = (if (i == 0) "" else "...") + m.substring(i, j).trim + (if (j == m.length) "" else "...")
        println(s"Found '$word' in ${tuple._1}:\n$snippet\n")

      case Success(tuple) =>
        println(s"Sorry, ${tuple._1} does not contain '$word'\n")

      case Failure(err) =>
        println(s"Error: Could not read from ${err.getMessage}\n")
    }(whenDone)
  }

  val urls2 = List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com")
  val signal1 = Promise[String]()
  urlSearch6("free", urls2) { signal1.success("done") }
  Await.ready(signal1.future, duration.Duration.Inf)

  val signal2 = Promise[String]()
  urlSearch6("free", Nil) { signal2.success("done") }
  Await.ready(signal2.future, duration.Duration.Inf)

  println("All done")
}
