package multi.futures

import multi.factorial
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

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
    Future(factorial(23456)).flatMap { y =>
      val f = Future(factorial(34567)).map { z => x + y + z }
      System.exit(0)
      f
    }
  }
  synchronized { wait() }
}
