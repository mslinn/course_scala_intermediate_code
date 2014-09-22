import concurrent.{Await, Future}
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global
import util.{Success, Failure}

/** WARNING: if you use concurrent.ExecutionContext.Implicits.global, daemon threads are used
 * once the program has reached the end of the main program, any other threads still executing
 * are terminated */

object FutureFun extends App {
//  val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
//  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(pool)

  //val urls2 = List("http://www.scalacourses.com", "http://www.not_really_here.com", "http://www.micronauticsresearch.com")
  val urls2: List[String] = List("http://www.scalacourses.com", "http://www.micronauticsresearch.com")

  def readUrl(url: String): String = io.Source.fromURL(url).mkString

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

  val futures: List[Future[String]] = urls2.map(u => Future(io.Source.fromURL(u).mkString))
  val fs: Future[List[String]] = Future.sequence(futures)

  for {
    contents: List[String] <- fs
    (url, content) <- urls2 zip contents if content.toLowerCase.contains("scala")
  } println(url)

  urlSearch("scala", urls2)
  synchronized { wait() } // let daemon threads continue; hit control-C to terminate
}

object ForComp1 extends App {
  val list: List[Int] = for {
    x <- List(1+2)
    y <- List(2+3)
    z <- List(4+5, 7)
  } yield x + y + z

  val option: Option[Int] = for {
    x <- Option(1+2)
    y <- Option(2+3) if y==x
    z <- Option(4+5) if z>y
  } yield x + y + z

  val f1 = Future(1+2)
  val f2 = Future(3+4)
  val f3 = Future(4+5)
  val future4: Future[Int] = for {
    x <- f1
    y <- f2
    z <- f3
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
  val sky = io.StdIn.readLine("What color is the sky? ").toLowerCase
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
  val sky = io.StdIn.readLine().toLowerCase
  val future4 = Future(1+2).withFilter {
    x => sky=="blue"
  }.flatMap { x =>
    Future(2+3).flatMap{ y =>
      Future(3+4).map { z => x + y + z }
    }
  }
  synchronized { wait() }
}
