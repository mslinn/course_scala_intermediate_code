package multi.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import scala.language.postfixOps

object FutureAwait extends App {
  def fetch(urlStr: String) = Future(io.Source.fromURL(urlStr).mkString)

  val result: Future[String] = Await.ready(fetch("http://www.scalacourses.com"), 20 seconds)
  Await.ready(fetch("http://scalacourses.com"), 1 hour)
  println("The future has completed within 1 hour")

  Await.ready(fetch("http://scalacourses.com"), Duration.Inf)
  println("The future has completed before infinity ended")

  try {
    Await.ready(fetch("http://scalacourses.com"), 1 nano)
  } catch {
    case te: java.util.concurrent.TimeoutException =>
      println("The future did not complete within 1 nanosecond")
  }
}


object FutureResult extends App {
  import multi.futures.FutureAwait.fetch

  val result = Await.result(fetch("http://www.scalacourses.com"), 2 hours)
  println("The future has completed within 2 hours.")

  val result2 = Await.result(fetch("http://www.scalacourses.com"), 1 hour).substring(0, 500).trim
  println("The future has completed within 1 hour.")
}


object FutureBadHabits extends App {
  val f = Future(io.Source.fromURL("http://www.scalacourses.com").mkString)
  val fvalTry: Try[String] = f.value.get // throws Exception because Future has not completed yet
  val fval: String = f.value.get.get
  val x: String = f.value.get.get.mkString.substring(0, 500).trim
}


object WaitExitDemo extends App {
  import scala.io.Source
  import scala.util.{Failure, Success}

  Future(Source.fromURL("http://www.scalacourses.com")) onComplete {
    case Success(iterator) ⇒
      println("\nFirst 500 characters of http://scalacourses.com:\n" + iterator.mkString.trim.substring(0, 500))
      System.exit(0)

    case Failure(throwable) ⇒
      println("\n" + throwable.getMessage)
      System.exit(-1)
  }
  println("End of mainline: suspending main thread in case any futures still need to complete.")
  synchronized { wait() }
}

object SignalDemo extends App {
  import scala.concurrent._
  import scala.io.Source
  import scala.util.{Failure, Success}

  val signal = Promise[String]()
  Future(Source.fromURL("http://www.scalacourses.com")) onComplete {
    case Success(iterator) ⇒
      println("\nFirst 500 characters of http://scalacourses.com:\n" + iterator.mkString.trim.substring(0, 500))
      signal.complete(Success("All done"))

    case Failure(throwable) ⇒
      println("\n" + throwable.getMessage)
      signal.complete(Failure(throwable))
  }

  Await.ready(signal.future, duration.Duration.Inf)
}
