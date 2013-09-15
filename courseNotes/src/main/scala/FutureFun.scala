import concurrent.{ExecutionContext, Await, Future}
import concurrent.duration._
import concurrent.forkjoin.{ForkJoinWorkerThread, ForkJoinPool}
import concurrent.ExecutionContext.Implicits.global
import util.{Success, Failure}
import io.Source

/** WARNING: if you use concurrent.ExecutionContext.Implicits.global, daemon threads are used
 * once the program has reached the end of the main program, any other threads still executing
 * are terminated */

object FutureFun extends App {
//  val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
//  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(pool)

  val urls2 = List("http://www.scalacourses.com", "http://www.not_really_here.com", "http://www.micronauticsresearch.com")

  def readUrl(url: String): String = Source.fromURL(url).mkString

  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map { url =>
      Future(readUrl(url)).recover {
        case e: Exception =>
          println(s"Handling URL read exception on $url")
          ""
      }
    }
    val sequence: Future[List[String]] = Future.sequence(futures2)
    Await.ready(sequence, 30 seconds) // block until all futures complete, timeout occurs, or a future fails
    println(s"""sequence ${if (sequence.isCompleted) "succeeded within timeout period" else "timed out"}""")
    for {
      (url, future) <- urls2 zip futures2
      contents: String <- future
    } {
      println(s"Scanning $url (${contents.length} characters)")
      if (contents.toLowerCase.contains(word)) {
        println(s"  $word was found in $url")
      } else {
        println(s"  $url did not contain $word")
      }
    }
    println()
  }

  urlSearch("scala", urls2)
  synchronized { wait() } // let daemon threads continue
}

object ForComp1 extends App {
  val future4: Future[Int] = for {
    x <- Future(1+2)
    y <- Future(2+3)
    z <- Future(4+5)
  } yield x + y + z
  future4 onComplete {
    case Success(r) =>
      println(s"Success: $r")
      System.exit(0)
    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(0)
  }
  synchronized { wait() }
}

object ForComp2 extends App {
  (for {
    x <- Future(1+2)
    y <- Future(2+3)
    z <- Future(4+5)
  } yield x + y + z) onComplete {
    case Success(r) =>
      println(s"Success: $r")
      System.exit(0)
    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(0)
  }
  synchronized { wait() }
}

object ForComp3 extends App {
  Future(1+2).flatMap { x =>
    Future(2+3).flatMap { y =>
      Future(4+5).map { z => x + y + z }
    }
  } onComplete {
    case Success(r) =>
      println(s"Success: $r")
      System.exit(0)
    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(0)
  }
  synchronized { wait() }
}

object ForComp4 extends App {
  print("What color is the sky? ")
  val sky = Console.readLine().toLowerCase
  val future4 = for {
    x <- Future(1+2)
    y <- Future(2+3)
    z <- Future(4+5)
    if sky == "blue"
  } yield x + y + z
  future4 onComplete {
    case Success(r) =>
      println(s"Success: $r")
      System.exit(0)
    case Failure(ex) =>
      println(s"Failure: $ex")
      System.exit(0)
  }
  synchronized { wait() }
}

object ForComp5 extends App {
  print("What color is the sky? ")
  val sky = Console.readLine().toLowerCase
  val future4 = Future(1+2).withFilter {
    x => sky=="blue"
  }.flatMap { x =>
    Future(2+3).flatMap{ y =>
      Future(3+4).map { z => x + y + z }
    }
  }
  synchronized { wait() }
}
