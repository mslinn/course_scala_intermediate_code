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

object FutureMixed extends App {
  import scala.concurrent._

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

  def snippet(word: String, string: String): String = {
    val m = string.trim.toLowerCase
    val i = math.max(0, m.indexOf(word) - 50)
    val j = math.min(m.length, i + 100)
    val snippet = (if (i == 0) "" else "...") + m.substring(i, j).trim + (if (j == m.length) "" else "...")
    snippet
  }

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
  def asapFutures[T](futures: Seq[Future[T]])(operation: Try[T]=>Unit)(whenDone: =>Unit={}): Unit = {
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

  def snippet(word: String, string: String): String = {
    val m = string.trim.toLowerCase
    val i = math.max(0, m.indexOf(word) - 50)
    val j = math.min(m.length, i + 100)
    val snippet = (if (i == 0) "" else "...") + m.substring(i, j).trim + (if (j == m.length) "" else "...")
    snippet
  }

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

  /** @param block is lazily evaluated and is used to create the Future.
    * @param ex is the usual ExecutionContext for the Future to run
    * @return Tuple containing the Future and a Function1[String,CancellationException].
    *         The Function1 returns None if Future has not been canceled, otherwise it returns Some(CancellationException))
    *         that contains the String passed to the function when the future was canceled.
   * */
def interruptableFuture[T](block: =>T)(implicit ex: ExecutionContext): (Future[T], String => Option[CancellationException]) = {
  val p = Promise[T]()
  val future = p.future
  val atomicReference = new java.util.concurrent.atomic.AtomicReference[Thread](null)
  p tryCompleteWith Future {
    val thread = Thread.currentThread
    atomicReference.synchronized { atomicReference.set(thread) }
    try block finally {
      atomicReference.synchronized { atomicReference getAndSet null } ne thread
    }
  }

  /** This method can be called multiple times */
  val cancelMe = (msg: String) => {
    if (p.isCompleted) {
      None
    } else {
      atomicReference.synchronized {
        Option(atomicReference getAndSet null) foreach { _.interrupt() }
      }
      val ex = new CancellationException(msg)
      p.tryFailure(ex)
      Some(ex)
    }
  }

  (future, cancelMe)
}

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
