package multi.futures

import java.io.{FileNotFoundException, IOException}
import java.util.concurrent.TimeoutException
import java.net.{MalformedURLException, UnknownHostException}
import multi._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object FutureFixtures {
  /** Blocks until contents of web page at urlStr or whatever recoveryFn contains becomes available, then prints contents.
   * @param urlStr String passed to java.net.URL that specifies the URL of a web page
   * @param msg Optional parameter that specifies a prefix message to be displayed before the web page contents
   * @param recoveryFn Function1[Future[String], Future[String]] which is a pluggable recovery strategy for failed web pages
   * @return Future of contents of web page at urlStr or recoverFn */
  def show(urlStr: String, msg: String="")(recoveryFn: Future[String] => Future[String]): Future[String] = {
    val future = recoveryFn(readUrlFuture(urlStr))
    println(s"$urlStr; ${ if (msg.length>0) s"$msg, " else "" }returning " + Await.result(future, 30 minutes))
    future
  }

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
}

object FutureFallbackTo extends App {
  println("Dot notation:\n" + Await.result(badHostFuture.fallbackTo(defaultFuture), 30 minutes))
  println("\n\nInfix notation:\n" + Await.result(badHostFuture fallbackTo defaultFuture, 30 minutes))
  println("\n\n")
  Await.result(badHostFuture fallbackTo badPageFuture, 30 minutes)
}

object FutureRecover extends App {
  import multi.futures.FutureFixtures._

  def doSomething(msg: String) = msg

  show(goodUrlStr1, "no problem") {
    _.recover { case e: UnknownHostException => doSomething("Handled UnknownHostException") }
  }

  show(badHostUrlStr) {
    _.recover { case e: UnknownHostException => doSomething(s"Handled UnknownHostException") }
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
    _.recover { case e: FileNotFoundException => doSomething("Handled FileNotFoundException") }
     .recover { case e: IOException           => doSomething("Handled IOException") }
     .recover { case e: MalformedURLException => doSomething("Handled MalformedURLException") }
     .recover { case e: UnknownHostException  => doSomething("Handled UnknownHostException") }
  }

  // This next expression causes the compiler to issue a warning. I explain why in the Future Combinators lecture
  // http://scalacourses.com/student/showLecture/176
  // Feel free to correct this code
  show(badHostUrlStr, "handle 4 Exception types in one PartialFunction using recover") {
    _.recover {
      case e: FileNotFoundException  => doSomething("Handled FileNotFoundException")
      case e: IOException            => doSomething("Handled IOException")
      case e: MalformedURLException  => doSomething("Handled MalformedURLException")
      case e: UnknownHostException   => doSomething("Handled UnknownHostException")
    }
  }
}

object FutureRecoverWith extends App {
  import multi.futures.FutureFixtures._

  show(goodUrlStr1, "no problem") {
    _.recoverWith { case e: UnknownHostException => Future.successful("Handled UnknownHostException") }
  }

  show(badHostUrlStr) {
    _.recoverWith { case e: UnknownHostException => Future.successful("Handled UnknownHostException") }
  }

  try {
    show(badHostUrlStr) {
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
}

object FutureCollect extends App {
  val allUrlsWithFutures: List[(String, Future[String])] =
    urls(includeBad = true) map { url => url -> readUrlFuture(url) }

  allUrlsWithFutures foreach {
    case tuple: (String, Future[String]) =>
      println(s"  Examining ${tuple._1}")
      tuple._2.collect {
        case content: String if content.toLowerCase.contains("scala") =>
          println(s"  Using Future.collect, scala was found in ${tuple._1}")
      }.recover { case _ =>
        println(s"  Future for ${tuple._1} failed")
      }
  }

  val futures = Future.sequence(allUrlsWithFutures.map { _._2 })
  Await.ready(futures, 30 minutes)
}

object FutureFilter extends App {
  1 to 10 foreach { i =>
    val future: Future[Int] = Future.successful(random.nextInt(100))
    val oddFuture: Future[Int]  = future filter { _ % 2 == 1 }
    val evenFuture: Future[Int] = future filter { _ % 2 == 0 }
    // Either oddFuture or evenFuture contains Failure(java.util.NoSuchElementException: Future.filter predicate is not satisfied)
    val oddOr24  = Await.result(oddFuture.recover      { case throwable: Throwable => 24},         30 seconds)
    val evenOr42 = Await.result(evenFuture.recover     { case throwable: Throwable => 42},         30 seconds)
    val all      = Await.result(evenFuture.recoverWith { case throwable: Throwable => oddFuture }, 30 seconds)
    println(f"$i%2.0f: oddOr24=$oddOr24; evenOr42=$evenOr42; all=$all")
  }
}

object FutureFlatMap extends App {
  case class User(name: String, privilege: List[String]) {
    /** simulate slow database access */
    def grantPrivilege(newPriv: String) = Future {
      Thread.sleep(random.nextInt(1000))
      if (System.currentTimeMillis % 3==0)
        throw new Exception("Unlucky time to grant privilege, not gonna do it!")
      copy(privilege = newPriv :: privilege)
    }
  }

  object User {
    private val userMap = Map(
      1L -> "Fred Flintstone",
      2L -> "Wilma Flintstone",
      3L -> "Barney Rubble",
      4L -> "Betty Rubble"
    )

    /** Simulate slow database access */
    def findById(id: Long): Future[User] = Future {
      Thread.sleep(random.nextInt(1000))
      User(userMap(id), Nil)
    }
  }

  val signal = Promise[String]()
  val user: Future[User] = User.findById(random.nextInt(4)+1)
    .flatMap { _.grantPrivilege("student") }
    .andThen {
      case Success(value)     => println(s"""${value.name}'s privilege is now: ${value.privilege.mkString(", ")}.""")
      case Failure(throwable) => println(s"Problem augmenting student privilege: ${throwable.getMessage}")
    }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureForeach extends App {
  val signal = Promise[String]()
  Future(factorial(12345)).foreach { value =>
    println(value)
    signal.success("All done")
  }
  Await.ready(signal.future, 30 minutes)
}

object FutureMap extends App {
  val x = Await.result(Future(factorial(12345)).map { _ == 0 }, 30 seconds)
  println(s"""x is ${ if (x) "even" else "odd" }""")
}

object FutureMapTo extends App {
  val x: Future[Any] = Future.successful(1)
  val y: Future[Int] = x.mapTo[Int]
  println(s"x = $x; y = $y")
}

object FutureTransform extends App {
  val f1 = Future.successful(random.nextInt(100))
    .filter(_%2==0)
    .transform(_/2, throwable => new Exception("Allergic to odd numbers... achoo!", throwable))
    .andThen { case Success(value) => println(s"The (even) random number divided by 2 = $value") }

  val f2 = Future(6/0)
    .transform(identity, throwable => new Exception("Something went wrong.", throwable))

  val signal = Promise[String]()
  Future.sequence(List(f1, f2))
    .andThen {
      case Success(value)     => println(value)
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureZip extends App {
  case class User(name: String, id: Long)

  def getUser: Future[User] = Future { // simulate slow database access
    Thread.sleep(random.nextInt(1000))
    User("Fred Flintstone", 123)
  }

  def lotteryNumber: Future[Int] = Future { // simulate slow database access
    Thread.sleep(random.nextInt(1000))
    random.nextInt(1000000)
  }

  val signal = Promise[String]()
  getUser zip lotteryNumber andThen {
    case Success(tuple)     => println(s"User ${tuple._1.name} with id ${tuple._1.id} has lucky number ${tuple._2}.")
    case Failure(throwable) => println(s"Problem: ${throwable.getMessage}")
  } andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureFind extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val signal = Promise[String]()
  Future
    .find(List(f1, f2, f3)) { _ % 2 == 0 }
    .andThen {
      case Success(Some(result)) => println(s"result = $result")
      case Success(None)         => println(s"No result was even")
      case Failure(throwable)    => println(throwable.getMessage)
    }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureFirstCompletedOf extends App {
  val f1 = Future(factorial(123))
  val f2 = Future(factorial(234))
  val f3 = Future(factorial(345))
  val signal = Promise[String]()
  Future
    .firstCompletedOf(List(f1, f2, f3))
    .andThen {
      case Success(result)    => println(s"result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureFold extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  val futures = List(f1, f2, f3)
  val signal = Promise[String]()

  Future
    .fold(futures)(BigInt(0))(_ + _)
    .andThen {
      case Success(result)    => println(s"fold addition result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen {
      case _ =>
        val bigMax: (BigInt, BigInt) => BigInt = (x: BigInt, y: BigInt) => if (x>y) x else y
        Future
          .fold(futures)(BigInt(0))(bigMax)
          .andThen {
            case Success(result)    => println(s"fold max result = $result")
            case Failure(throwable) => println(throwable.getMessage)
          }.andThen { case _ => signal.success("All done") }
    }
  Await.result(signal.future, 30 minutes)
}

object FutureReduce extends App {
  val f1 = Future(factorial(123))
  val f2 = Future(factorial(234))
  val f3 = Future(factorial(345))
  val futures = List(f1, f2, f3)
  val bigMax = (x: BigInt, y: BigInt) => if (x>y) x else y
  val signal = Promise[String]()

  Future
    .reduce(futures)(_+_)
    .andThen {
    case Success(result)    => println(s"Reduce addition result = $result")
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen {
    case _ =>
      Future
        .reduce(futures)(bigMax)
        .andThen {
          case Success(result)    => println(s"Reduce max result = $result")
          case Failure(throwable) => println(throwable.getMessage)
        }.andThen { case _ => signal.success("All done") }
  }
  Await.ready(signal.future, 30 minutes)
}

object FutureSequence extends App {
  val f1 = Future(factorial(123))
  val f2 = Future(factorial(234))
  val f3 = Future(factorial(345))
  val futures = List(f1, f2, f3)
  val signal = Promise[String]()

  Future
    .sequence(futures)
    .andThen {
      case Success(result) => println(s"Sequence result = $result")
      case Failure(throwable) => println(throwable.getMessage)
    }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}

object FutureTraverse extends App {
  val signal = Promise[String]()
  Future.traverse(List(12356, 234, 345)) { x =>
    Future(factorial(x)) andThen { case Success(value) => println(s"""factorial($x)=$value""") }
  }.andThen {
    case Success(list)      => println(list.mkString("traverse results:\n  ", "\n  ", ""))
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen { case _ => signal.success("All done") }
  Await.ready(signal.future, 30 minutes)
}
