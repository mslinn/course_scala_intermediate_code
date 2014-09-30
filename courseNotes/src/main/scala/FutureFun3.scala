import scala.concurrent.{ExecutionContext, Await, Future}
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global
import util.{Success, Failure}
import FutureFun2.factorial

object ForComp1 extends App {
  val list: List[BigInt] = for {
    x <- List(factorial(12345))
    y <- List(factorial(23456))
    z <- List(factorial(34567))
  } yield x + y + z

  val option: Option[BigInt] = for {
    x <- Option(factorial(12345))
    y <- Option(factorial(23456)) if y==x
    z <- Option(4+5) if z>y
  } yield x + y + z

  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(34567))
  val f3 = Future(factorial(34567))
  val future4: Future[BigInt] = for {
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
    x <- Future(factorial(12345))
    y <- Future(factorial(23456))
    z <- Future(factorial(34567))
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
  Future(factorial(12345)).flatMap { x =>
    Future(factorial(23456)).flatMap { y =>
      Future(factorial(34567)).map { z => x + y + z }
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
    x <- Future(factorial(12345))
    y <- Future(factorial(23456))
    z <- Future(factorial(34567))
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
  val future4 = Future(factorial(12345)).withFilter {
    x => sky=="blue"
  }.flatMap { x =>
    Future(factorial(23456)).flatMap{ y =>
      Future(factorial(34567)).map { z => x + y + z }
    }
  }
  synchronized { wait() }
}

object FutureFixtures {
  def urls(includeBad: Boolean = false): List[String] =
    List("http://www.scalacourses.com", "http://www.micronauticsresearch.com") ::: (if (includeBad) List("http://www.not_really_here.com") else Nil)

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
    try {
      Await.ready(sequence, 30 seconds) // block until all futures complete, timeout occurs, or a future fails
      println( s"""sequence ${if (sequence.isCompleted) "succeeded within timeout period" else "timed out"}""")
      for {
        (url, future) <- urls zip futures2
        contents: String <- future
      } {
        println(s"Scanning $url (${contents.length} characters)")
        if (contents.toLowerCase.contains(word)) {
          println(s"  $word was found in $url")
        } else {
          println(s"  $url did not contain $word")
        }
      }
    } catch {
      case te: java.util.concurrent.TimeoutException =>
        println("Futures timed out, is there an Internet connection?")
    }
    println()
  }

  def listOfFutures(includeBad: Boolean = false): List[Future[String]] = urls(includeBad).map { url => Future(readUrl(url))}

  def futureOfList(includeBad: Boolean = false): Future[List[String]] = Future.sequence(listOfFutures(includeBad))

  urlSearch("scala", urls())
  synchronized { wait() } // let daemon threads continue; hit control-C to terminate
}

object FutureFallbackTo extends App {
  import language.postfixOps
  import FutureFixtures._

  val fZero: Future[Int] = Future(5 / 0)
  val defaultFuture: Future[Int] = Future.successful(42)
  val result: Future[Int] = fZero.fallbackTo(defaultFuture)
  // can also write:
  // val result = fZero fallbackTo defaultFuture

  Future(readUrl(urls().head))
    .fallbackTo(Future(readUrl(urls().take(2).head)))
    .fallbackTo(Future.successful("This is the default value"))
}

object FutureRecover extends App {
  import language.postfixOps

  Future(6 / 2).recover { case e: ArithmeticException => 42 } // new Future value: 3
  Future(6 / 0).recover { case e: ArithmeticException => 42 } // new Future value: 42
  // new Future value: java.lang.ArithmeticException("/ by zero")
  Future(6 / 0).recover { case e: NoSuchElementException => 42 }
  Future(6 / 0)
    .recover { case e: NoSuchElementException => 42 }
    .recover { case e: java.io.IOException => 43 }
    .recover { case e: java.net.MalformedURLException => 44 }
  Future(6 / 0).recover {
    case e: NoSuchElementException => 42
    case e: java.io.IOException => 43
    case e: java.net.MalformedURLException => 44
  }
}

object FutureRecoverWith extends App {
  import language.postfixOps

  val defaultFuture: Future[Int] = Future.successful(42)
  Future(6 / 0).recoverWith { case e: ArithmeticException => defaultFuture}
  Future(6 / 0).recoverWith { case e: NoSuchElementException => defaultFuture}

  val f5: Future[Int] = Future.successful(new util.Random().nextInt(100))
  val q = f5.collect {
    case value: Int if value > 50 => value * 2
  }.recover {
    case throwable: Throwable => 42 // default value
  }
}

object FutureCollect extends App {
  import language.postfixOps
  import FutureFixtures._
  val allUrlsWithFutures: List[(String, Future[String])] = urls(includeBad = true) zip listOfFutures(includeBad = true)
  allUrlsWithFutures foreach {
    case tuple: (String, Future[String]) =>
      println(s"  Examining ${tuple._1}")
      tuple._2.collect {
        // this is Future.collect
        case content: String if content.toLowerCase.contains("scala") =>
          println(s"  Using Future.collect, scala was found in ${tuple._1}")
      }.recover { case throwable: Throwable =>
        println(s"  Future for ${tuple._1} failed")
      }
  }

  Future(readUrl(urls().head))
    .collect { case value: String => value.substring(0, math.min(100, value.length))} // only performed if future succeeded
    .fallbackTo(Future(readUrl(urls().take(2).head)))
    .collect { case value: String => value.substring(0, math.min(100, value.length))} // only performed if future succeeded
    .fallbackTo(Future.successful("This is the default value"))
}

object FutureFilter extends App {
  import language.postfixOps

  val f5: Future[Int] = Future.successful(new util.Random().nextInt(100))
  val g: Future[Int] = f5 filter {
    _ % 2 == 1
  }
  // This future succeeded, contains 5
  val h: Future[Int] = f5 filter {
    _ % 2 == 0
  }
  // This future contains Failure(java.util.NoSuchElementException: Future.filter predicate is not satisfied)
  val r1 = Await.result(g, Duration.Zero)
  // evaluates to 5
  val r2 = Await.result(h.recover { case throwable: Throwable => 42}, Duration.Zero) // evaluates to 42
}

object FutureFlatMap extends App {
  import language.postfixOps

  case class User(name: String, privilege: List[String]) {
    def grantPrivilege(newPriv: String) = Future {
      // simulate slow database access
      Thread.sleep(149)
      copy(privilege = newPriv :: privilege)
    }
  }

  def getUser: Future[User] = Future {
    // simulate slow database access
    Thread.sleep(150)
    User("bogusName", Nil)
  }

  val boostedUser = getUser.flatMap {
    _.grantPrivilege("student")
  }
  boostedUser.onComplete {
    case Success(value) => println(s"Student privilege is now: $value")
    case Failure(throwable) => println("Problem augmenting student privilege: " + throwable.getMessage)
  }
}

object FutureZip extends App {
  import language.postfixOps
  import FutureFixtures._

  implicit val context = MultiThreading.executionContext()

  for {
    contents: List[String] <- futureOfList() // all futures must succeed in order for url to be printed
    (url, content) <- urls() zip contents if content.toLowerCase.contains("scala")
  } println(url)
}
