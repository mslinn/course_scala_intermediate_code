package multi.futures

import multi._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Try, Failure, Success}
import scala.language.postfixOps

object FutureAwait extends App {
  Await.ready(readUrlFuture("http://www.scalacourses.com"), 20 seconds)
  Await.ready(readUrlFuture("http://www.micronauticsresearch.com"), 1 hour)
  println("The future has completed within 1 hour")

  Await.ready(readUrlFuture("http://www.scalacourses.com"), Duration.Inf)
  println("The future completed before the end of time")

  try {
    Await.ready(readUrlFuture("http://www.scalacourses.com"), Duration.Zero)
  } catch {
    case te: java.util.concurrent.TimeoutException =>
      println("The future was not already complete")
  }
}


object FutureResult extends App {
  val result = Await.result(readUrlFuture("http://www.scalacourses.com"), 2 hours)
  println(s"The future completed within 2 hours with a result of ${result.length} characters.")

  val result2 = Await.result(readUrlFuture("http://www.scalacourses.com"), 1 hour)
  println(s"The future has completed within 1 hour with a result of ${result2.length} characters.")
}


object FutureBadHabits extends App {
  val future: Future[String] = readUrlFuture("http://www.scalacourses.com")
  val fValueTry: Try[String] = future.value.get // throws Exception because Future has not completed yet
  val futureValue: String = future.value.get.get
}


object WaitExitDemo extends App {
  readUrlFuture("http://www.scalacourses.com") onComplete {
    case Success(value) ⇒
      println(s"\nFirst 500 characters of http://scalacourses.com:\n$value")
      System.exit(0)

    case Failure(throwable) ⇒
      println("\n" + throwable.getMessage)
      System.exit(-1)
  }
  println("End of mainline: suspending main thread in case any futures still need to complete.")
  synchronized { wait() }
}

object SignalDemo extends App {
  val signal = Promise[String]()
  readUrlFuture("http://www.scalacourses.com") onComplete {
    case Success(value) ⇒
      println("\nFirst 500 characters of http://scalacourses.com:\n$value")
      signal.complete(Success("All done"))

    case Failure(throwable) ⇒
      println("\n" + throwable.getMessage)
      signal.complete(Failure(throwable))
  }

  Await.ready(signal.future, concurrent.duration.Duration.Inf)
}
