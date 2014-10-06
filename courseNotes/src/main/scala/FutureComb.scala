import scala.concurrent.{Await, Future}
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global
import util.{Success, Failure}
import FutureFun2.factorial
import language.postfixOps

object FutureFixtures {
  def urls(includeBad: Boolean = false): List[String] =
    List("http://www.scalacourses.com", "http://www.micronauticsresearch.com") :::
      (if (includeBad) List("http://www.not_really_here.com") else Nil)

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

  val boostedUser: Future[User] = getUser.flatMap {
    _.grantPrivilege("student")
  }
  boostedUser.andThen {
    case Success(value) => println(s"Student privilege is now: $value")
    case Failure(throwable) => println("Problem augmenting student privilege: " + throwable.getMessage)
  }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureForeach extends App {
  import concurrent.ExecutionContext.Implicits.global

  Future(factorial(12345)).foreach(println)
}

object FutureMap extends App {
  import concurrent.ExecutionContext.Implicits.global

  val x = Future(factorial(12345)).map { _ == 0 }
  println(s"x = $x")
}

object FutureMapTo extends App {
  val x: Future[Any] = Future.successful(1)
  val y: Future[Int] = x.mapTo[Int]
  println(s"x = $x; y = $y")
}

object FutureTransform extends App {
  import concurrent.ExecutionContext.Implicits.global

  Future(6/0).transform (
    identity,
    throwable => new Exception("Something went wrong", throwable)
  ).andThen {
    case Success(value) => println(value)
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureZip extends App {
  import language.postfixOps

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
  Future.find(List(f1, f2, f3)) { _ % 2 == 0 }.andThen {
    case Success(result) => println(s"result = $result")
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}

object FutureFirstCompletedOf extends App {
  val f1 = Future(factorial(12345))
  val f2 = Future(factorial(23456))
  val f3 = Future(factorial(34567))
  Future.firstCompletedOf(List(f1, f2, f3)).andThen {
    case Success(result) => println(s"result = $result")
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

  Future.fold(futures)(BigInt(0))(_+_).andThen {
    case Success(result) => println(s"fold addition result = $result")
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen {
    case _ =>
      Future.fold(futures)(BigInt(0))(bigMax).andThen {
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

  Future.reduce(futures)(_+_).andThen {
    case Success(result) => println(s"reduce addition result = $result")
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen {
    case _ =>
      Future.reduce(futures)(bigMax).andThen {
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

  Future.sequence(futures).andThen {
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
  Future.traverse(list) { x => Future(factorial(x)) }.andThen {
    case Success(factorialList) => factorialList.foreach { result => println(s"traverse result = $result") }
    case Failure(throwable) => println(throwable.getMessage)
  }.andThen { case _ => System.exit(0) }
  synchronized { wait() }
}