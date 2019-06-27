package multi.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NoStackTrace
import scala.util.{Failure, Success}

object FutureCallback extends App {
  val promises: Vector[Promise[String]] =
    Promise.successful("Hi there") +: (1 to 4).map { _ => Promise[String]() }.toVector

  def promiseStatus(i: Int): Unit =
    if (promises(i).isCompleted) {
      promises(i).future.value.get match {
        case Success(value) => println(s"promises($i).future.value='$value'.")
        case Failure(exception) => println(s"promises($i).future exception message: '${exception.getMessage}'.")
      }
    } else println(s"promises($i) is pending.")

  def promiseStatuses(msg: String): Unit = {
    println(s"\n== $msg ==")
    0 until promises.length foreach { promiseStatus }
    println()
  }

  promiseStatuses("Five Promises Started")
  promises(0).future onComplete {
    case Success(value) => println(s"promises(0) completed successfully with value='$value'.")
    // Caution: you should also provide the Failure case. This is an example of bad habits
  }

  promises(1).future onComplete {
    case Success(value) => println(s"promises(1) onComplete success: value='$value'.")
    case Failure(throwable) => println(s"promises(1) onComplete exception; message: '${ throwable.getMessage }'.")
  }
  promises(1).future onComplete {
    case Success(value) => println(s"promises(1) onComplete; value='$value'.")
    // Caution: you should also provide the Failure case. This is an example of bad habits
  }

  promises(1).future onComplete {
    // Caution: you should also provide the Success case. This is an example of bad habits
    case Failure(throwable) => println(s"promises(1) onComplete; exception message: '${ throwable.getMessage }'.")
  }
  promises(1).future andThen {
    case Success(value) => println(s"promises(1) andThen success: value='$value'.")
    case Failure(throwable) => println(s"promises(1) andThen exception; message: '${ throwable.getMessage }'.")
  }
  promises(1).success("The tao that is knowable is not the true tao")
  //promises(1).complete(Success("The tao that is knowable is not the true tao")) // does same thing
  promiseStatuses("After Promises(1) Completed")

  println("\nPartial Functions can have many cases")
  val optionIntPromise1 = Promise[Option[Int]]()
  optionIntPromise1.future onComplete {
    case Success(Some(value)) => println(s"The value $value only is matched if the future returns an Option")
    case Success(None) => println(s"None could only be matched if the future returns an Option")
    // Caution: you should also provide the Failure case. This is an example of bad habits
  }
  optionIntPromise1.success(Some(3))
  Await.ready(optionIntPromise1.future, 30 minutes)

  val optionFilePromise1 = Promise[Option[java.io.File]]()
  optionFilePromise1.future onComplete {
    // Caution: you should also provide the Success case. This is an example of bad habits
    case Failure(fnfe: java.io.FileNotFoundException) => println(s"Hey, you are missing a file! ${ fnfe.getMessage }")
    case Failure(ioe: java.io.IOException) => println(s"Could be burgler? ${ ioe.getMessage }")
    case Failure(throwable) => println(s"optionFilePromise1 throwable message: '${ throwable.getMessage }'.")
  }
  optionFilePromise1.failure(new java.io.IOException("Oh noes!"))
  Await.ready(optionFilePromise1.future, 30 minutes)

  val optionFilePromise2 = Promise[Option[java.io.File]]()
  optionFilePromise2.future andThen {
    case Success(Some(value)) => println(s"The value $value only is matched if the future returns an Option")
    case Success(None) => println(s"None could only be matched if the future returns an Option")
    case Failure(fnfe: java.io.FileNotFoundException) => println(s"Hey, you are missing a file! ${ fnfe.getMessage }")
    case Failure(ioe: java.io.IOException) => println(s"Could be burgler? ${ ioe.getMessage }")
    case Failure(throwable) => println(s"optionFilePromise2 exception message: '${ throwable.getMessage }'.")
  }
  optionFilePromise2.failure(new java.io.IOException("Oh noes!"))
  Await.ready(optionFilePromise2.future, 30 minutes)

  promises(2).future andThen {
    case Success(value) => println(s"promises(2) andThen success value='$value'.")
    case Failure(throwable) => println(s"promises(2) andThen exception: '${ throwable.getMessage }'.")
  } andThen {
    case _ => println("promises(2).andThen.andThen - Will that be all, master?")
  }
  promises(2).complete(Success("Great achievement looks incomplete, yet it works perfectly"))
  promiseStatuses("After Promises(2) Completed")

  promises(3).future onComplete {
    // Caution: you should also provide the Success case. This is an example of bad habits
    case Failure(exception) => println(s"promises(3) completed with exception message: '${ exception.getMessage }'.")
  }

  class ExceptTrace(msg: String) extends Exception(msg) with NoStackTrace
  promises(3).failure(new ExceptTrace("Kaboom"))
  promiseStatuses("After Promises(3) Completed")

  object TheExceptTrace extends Exception("Kablam") with NoStackTrace
  promises(4).complete(Failure(TheExceptTrace))
  promises(4).future onComplete (_ => promiseStatuses("Five Promises Concluded"))

  val f1 = Future(multi.factorial(12345))
  f1 onComplete { // might not generate output before System.exit (run program several times to see)
    case Success(value) => println(s"factorial success #1: factorial(12345)=$value")
    case Failure(exception) => println(s"factorial onComplete completed with exception message: '${exception.getMessage}'.")
  }

  val f2 = Future(multi.factorial(12345)) andThen { // always works
    case Success(value) => println(s"factorial success #2: factorial(12345)=$value")
    case Failure(exception) => println(s"factorial andThen completed with exception message: '${exception.getMessage}'.")
  }

  Future.sequence(List(f1, f2) ::: promises.map(_.future).toList) andThen { case _ => System.exit(0) }

  synchronized { wait() }
}
