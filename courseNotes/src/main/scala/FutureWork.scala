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
      } println(url)
    }

  urlSearch("e", List("http://micronauticsresearch.com"))
  urlSearch("e", List("http://scalacourses.com"))
  urlSearch("e", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

  def slowUrlSearch(word: String, urls: List[String]): Unit = {
    for {
      url <- urls
      contents <- Future(io.Source.fromURL(url).mkString)
      if contents.toLowerCase.contains(word)
    } println(url)
  }

  slowUrlSearch("e", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

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
    } println(url)
  }

  urlSearch2("free", urls2)
  urlSearch2("scala", urls2)

  def urlSearch3(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    concurrent.Await.ready(Future.sequence(futures2), Duration.Inf) // block until all futures complete
    for {
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } println(url)
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
      } println(s"'$word' was found in $url")
    }

  def urlSearch5(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    for {
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } println(url)
  }
}

object FutureSelect extends App {
  import scala.concurrent._
  import scala.util._
  import scala.annotation.tailrec
  import java.util.concurrent.{Executors,ThreadPoolExecutor,TimeUnit}

  /** @return the first future to complete, with the remainder of the Futures as a sequence.
   * @param fs a scala.collection.Seq
   * @author Victor Klang (https://gist.github.com/4488970) */
  def select[A](fs: Seq[Future[A]])(implicit ec: ExecutionContext): Future[(Try[A], Seq[Future[A]])] = {
    @tailrec
    def stripe(p: Promise[(Try[A], Seq[Future[A]])],
               heads: Seq[Future[A]],
               elem: Future[A],
               tail: Seq[Future[A]]): Future[(Try[A], Seq[Future[A]])] = {
      elem onComplete { res => if (!p.isCompleted) p.trySuccess((res, heads ++ tail)) }
      if (tail.isEmpty) p.future
      else stripe(p, heads :+ elem, tail.head, tail.tail)
    }

    if (fs.isEmpty) Future.failed(new IllegalArgumentException("List of futures is empty"))
    else stripe(Promise(), fs.genericBuilder[Future[A]].result(), fs.head, fs.tail)
  }


  /** Apply a function over a sequence of futures as soon as each future completes.
    * @param futures sequence of futures to operate on
    * @param operation function to apply on each Future value as soon as they complete
    * @param whenDone block of code to execute when all futures have been processed
    * @author David Crosson
    * @author Mike Slinn */
  def asapFutures[T](futures: Seq[Future[T]])(operation: Try[T]=>Unit)(whenDone: =>Unit): Unit = {
    if (futures.nonEmpty) {
      val nextOne = select(futures)
      nextOne.andThen {
        case Success((result, remains)) =>
          operation(result)
          asapFutures(remains)(operation)(whenDone)
        case Failure(ex) =>
          operation(Failure[T](ex))
      }.andThen {
        case _ =>
          if (futures.size==1)
            whenDone
      }
    }
  }

  /** Simulates doing work and sometimes throwing errors */
  def doWork(inS: Int):String = {
    Thread.sleep(inS * 1000)
    if (inS > 10 && inS < 15) throw new RuntimeException("Simulated exception")
    s"slept ${inS}s"
  }

  val allFutures = concurrent.Promise[String]()
  val workToDo = List(4, 11, 2, 8, 1, 13, 15)
  val workToDoFutures = workToDo.map { t => Future { doWork(t) } }

  asapFutures(workToDoFutures) {
    case Success(msg) => println(s"OK  - $msg")
    case Failure(err) => println(s"Not OK - ${err.getMessage}")
  } { allFutures.success("done") }

  concurrent.Await.result(allFutures.future, concurrent.duration.Duration.Inf)
}