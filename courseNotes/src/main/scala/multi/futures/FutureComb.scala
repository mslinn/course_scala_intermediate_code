package multi.futures

import java.io.{FileNotFoundException, IOException}
import java.util.concurrent.TimeoutException
import java.net.{MalformedURLException, UnknownHostException}
import multi._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object FutureFixtures {
  lazy val goodUrlStr1       = "http://www.scalacourses.com"
  lazy val goodUrlStr2       = "http://www.micronauticsresearch.com"
  lazy val badHostUrlStr     = "http://www.not_really_here.com"
  lazy val badPageUrlStr     = "http://scalacourses.com/noPageHere"
  lazy val badProtocolUrlStr = "blah://scalacourses.com"

  lazy val badHostFuture: Future[String]     = readUrlFuture(badHostUrlStr)
  lazy val badPageFuture: Future[String]     = readUrlFuture(badHostUrlStr)
  lazy val badProtocolFuture: Future[String] = readUrlFuture(badHostUrlStr)
  lazy val defaultFuture: Future[String]     = readUrlFuture(goodUrlStr1)

  /** Blocks until contents of web page at urlStr or whatever recoveryFn contains becomes available, then prints contents.
   * @param urlStr String passed to java.net.URL that specifies the URL of a web page
   * @param msg Optional parameter that specifies a prefix message to be displayed before the web page contents
   * @param recoveryFn Function1[Future[String], Future[String]] which is a pluggable recovery strategy for failed web pages
   * @return Future of contents of web page at urlStr or recoverFn */
  def show(urlStr: String, msg: String="")(recoveryFn: Future[String] => Future[String]): Future[String] = {
    val future = recoveryFn(readUrlFuture(urlStr))
    println(s"$urlStr; ${ if (msg.length>0) s"$msg," else "" }returning " + Await.result(future, 30 minutes))
    future
  }

  def urls(includeBad: Boolean=false): List[String] =
    List(goodUrlStr1, goodUrlStr1) :::
    (if (includeBad) List(badHostUrlStr, badPageUrlStr, badProtocolUrlStr) else Nil)


  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map { url =>
      readUrlFuture(url).recover {
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
      case te: TimeoutException =>
        println("Futures timed out, is there an Internet connection?")
    }
    println()
  }

  def listOfFutures(includeBad: Boolean = false): List[Future[String]] =
    urls(includeBad).map { readUrlFuture(_) }

  def futureOfList(includeBad: Boolean = false): Future[List[String]] = Future.sequence(listOfFutures(includeBad))
}

object FutureFallbackTo extends App {
  import multi.futures.FutureFixtures._

  val result: Future[String] = badHostFuture.fallbackTo(defaultFuture)
  // can also write using infix notation:
  // val result = badHostFuture fallbackTo defaultFuture
  println(Await.result(result, 10 minutes))
}

object FutureRecover extends App {
  import multi.futures.FutureFixtures._

  show(goodUrlStr1, "no problem") {
    _.recover { case e: UnknownHostException => "Handled UnknownHostException" }
  }

  show(badHostUrlStr) {
    _.recover { case e: UnknownHostException => s"Handled UnknownHostException" }
  }

  try {
    show(badHostUrlStr) {
      _.recover {
        case e: NoSuchElementException => throw new Exception("Should not need to handle NoSuchElementException")
      }
    }
  } catch {
    case e: Exception =>
      println(s"Did not handle ${e.getClass.getName} exception for $badHostUrlStr")
  }

  // This next expression causes the compiler to issue a warning. I explain why in the Future Combinators lecture
  // http://scalacourses.com/student/showLecture/176
  // Feel free to correct this code
  show(badHostUrlStr, "handle 4 Exception types in 4 PartialFunctions using recover") {
    _.recover { case e: FileNotFoundException => "Handled FileNotFoundException" }
     .recover { case e: IOException           => "Handled IOException" }
     .recover { case e: MalformedURLException => "Handled MalformedURLException" }
     .recover { case e: UnknownHostException  => "Handled UnknownHostException" }
  }

  // This next expression causes the compiler to issue a warning. I explain why in the Future Combinators lecture
  // http://scalacourses.com/student/showLecture/176
  // Feel free to correct this code
  show(badHostUrlStr, "handle 4 Exception types in one PartialFunction") {
    _.recover {
      case e: FileNotFoundException  => "Handled FileNotFoundException"
      case e: IOException            => "Handled IOException"
      case e: MalformedURLException  => "Handled MalformedURLException"
      case e: UnknownHostException   => "Handled UnknownHostException"
    }
  }
}

object FutureRecoverWith extends App {
  import multi.futures.FutureFixtures._

  show(goodUrlStr1, "no problem") {
    _.recoverWith { case e: UnknownHostException => Future.successful("Handled UnknownHostException") }
  }

  show(badHostUrlStr, "handle UnknownHostException") {
    _.recoverWith { case e: UnknownHostException => Future.successful("Handled UnknownHostException") }
  }

  try {
    show(badHostUrlStr, "handle NoSuchElementException") {
      _.recoverWith {
        case e: NoSuchElementException =>
          Future.failed(new Exception("Should not need to handle NoSuchElementException"))
      }
    }
  } catch {
    case e: Exception =>
      println(s"Did not handle ${e.getClass.getName} exception for $badHostUrlStr")
  }

  show(badHostUrlStr, "handle 4 Exception types in 4 PartialFunctions using recoverWith") {
    _.recoverWith { case e: FileNotFoundException => defaultFuture}
     .recoverWith { case e: IOException           => defaultFuture}
     .recoverWith { case e: MalformedURLException => defaultFuture}
     .recoverWith { case e: UnknownHostException  => defaultFuture}
  }

  show(badHostUrlStr, "handle 4 Exception types in 1 PartialFunction using recoverWith") {
    _.recoverWith {
      case e: FileNotFoundException => defaultFuture
      case e: IOException           => defaultFuture
      case e: MalformedURLException => defaultFuture
      case e: UnknownHostException  => defaultFuture
    }
  }

  val f5: Future[Int] = Future.successful(new util.Random().nextInt(100))
  val q = f5.collect {
    case value: Int if value > 50 => value * 2
  }.recover {
    case throwable: Throwable => 42 // default value
  }
}

object FutureCollect extends App {
  import multi.futures.FutureFixtures._

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

  val signal = concurrent.Promise[String]()
  readUrlFuture(urls().head)
    .collect { case value: String => value } // only performed if future succeeded
    .fallbackTo(readUrlFuture(urls().take(2).head))
    .collect { case value: String => value } // only performed if future succeeded
    .fallbackTo(Future.successful("This is the default value"))
    .andThen { case _ => signal.success("All done") }
  Await.result(signal.future, concurrent.duration.Duration.Inf)
}

object FutureFilter extends App {
  val f5: Future[Int] = Future.successful(new util.Random().nextInt(100))
  val g: Future[Int] = f5 filter { _ % 2 == 1 }
  // This future succeeded, contains 5
  val h: Future[Int] = f5 filter { _ % 2 == 0}
  // This future contains Failure(java.util.NoSuchElementException: Future.filter predicate is not satisfied)
  val r1 = Await.result(g, Duration.Zero)
  // evaluates to 5
  val r2 = Await.ready(h.recover { case throwable: Throwable => 42}, Duration.Zero) // evaluates to 42
}

object FutureFlatMap extends App {
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

  val boostedUser: Future[User] = getUser
    .flatMap { _.grantPrivilege("student") }
    .andThen {
      case Success(value) => println(s"Student privilege is now: $value")
      case Failure(throwable) => println("Problem augmenting student privilege: " + throwable.getMessage)
    }
    .andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureForeach extends App {
  Future(factorial(12345)).foreach(println)
}

object FutureMap extends App {
  val x = Future(factorial(12345)).map { _ == 0 }
  println(s"x = $x")
}

object FutureMapTo extends App {
  val x: Future[Any] = Future.successful(1)
  val y: Future[Int] = x.mapTo[Int]
  println(s"x = $x; y = $y")
}

object FutureTransform extends App {
  Future(6/0)
    .transform(identity, throwable => new Exception("Something went wrong", throwable)
    ).andThen {
      case Success(value) => println(value)
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureZip extends App {
  case class User(name: String, id: Long)

  def getUser: Future[User] = Future {
    // simulate slow database access
    Thread.sleep(150)
    User("Fred Flintstone", 123)
  }

  def lotteryNumber: Future[Int] = Future {
    // simulate slow database access
    Thread.sleep(150)
    new util.Random().nextInt()
  }

  val luckyUser: Future[(User, Int)] = getUser zip lotteryNumber
  luckyUser.andThen {
    case Success(value) => println(s"User lottery tuple: $value")
    case Failure(throwable) => println("Problem: " + throwable.getMessage)
  }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureFind extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  Future
    .find(List(f1, f2, f3)) { _ % 2 == 0 }
    .andThen {
      case Success(result) => println(s"result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureFirstCompletedOf extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  Future
    .firstCompletedOf(List(f1, f2, f3))
    .andThen {
      case Success(result)    => println(s"result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureFold extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val futures = List(f1, f2, f3)
  val bigMax = (x: BigInt, y: BigInt) => if (x>y) x else y

  Future
    .fold(futures)(BigInt(0))(_+_)
    .andThen {
      case Success(result) => println(s"fold addition result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen {
      case _ =>
        Future
          .fold(futures)(BigInt(0))(bigMax)
          .andThen {
            case Success(result) => println(s"fold max result = $result")
            case Failure(throwable) => println(throwable.getMessage)
          }.andThen { case _ => System.exit(0) }
    }
  synchronized { wait() }
}

object FutureReduce extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val futures = List(f1, f2, f3)
  val bigMax = (x: BigInt, y: BigInt) => if (x>y) x else y

  Future
    .reduce(futures)(_+_)
    .andThen {
    case Success(result) => println(s"reduce addition result = $result")
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen {
    case _ =>
      Future
        .reduce(futures)(bigMax)
        .andThen {
          case Success(result) => println(s"reduce max result = $result")
          case Failure(throwable) => println(throwable.getMessage)
        }.andThen { case _ => System.exit(0) }
  }
  synchronized { wait() }
}

object FutureSequence extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val futures = List(f1, f2, f3)

  Future
    .sequence(futures)
    .andThen {
      case Success(result) => println(s"reduce result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureTraverse extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val list = List(1234, 2345, 3456)
  Future
    .traverse(list) { x => Future(factorial(x)) }
    .andThen {
      case Success(factorialList) => factorialList.foreach { result => println(s"traverse result = $result") }
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}
