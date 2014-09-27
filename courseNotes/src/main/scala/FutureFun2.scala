import concurrent._
import concurrent.ExecutionContext.Implicits.global
import io.Source
import scala.util.control.NoStackTrace
import scala.util.{Success,Failure}

object FutureFun2 extends App {
  val promise1, promise2, promise3, promise4 = Promise[String]()
  println(s"promise1.isCompleted=${promise1.isCompleted}")
  println(s"promise4.isCompleted-${promise4.isCompleted}")
  println(s"promise1.future.isCompleted=${promise1.future.isCompleted}")
  println(s"promise4.future.isCompleted-${promise4.future.isCompleted}")

  promise1.future.onSuccess {
    case value ⇒ println(s"onSuccess: promise1 value=$value")
  }
  promise1.success(s"Promise1 completed successfully")
  println(s"promise1.isCompleted=${promise1.isCompleted}")
  println(s"promise1.future.isCompleted=${promise1.future.isCompleted}")

  promise2.future.onSuccess {
    case value ⇒ println(s"onSuccess: promise2 value=$value")
  }
  promise2.complete(Success("Promise2 completed with Try/Success"))

  class ExceptTrace(msg: String) extends Exception(msg) with NoStackTrace
  promise3.failure(new ExceptTrace("Boom!"))

  object TheExceptTrace extends Exception("Boom!") with NoStackTrace
  promise4.complete(Failure(TheExceptTrace))

  println(s"promise1.future.value=${promise1.future.value}")
  println(s"promise2.future.failed=${promise2.future.failed}")
  println(s"promise3.future.value=${promise3.future.value}")
  println(s"promise4.future.value=${promise4.future.value}")

  val f = Future(Source.fromURL("http://www.scalacourses.com"))
  println(s"f.isCompleted=${f.isCompleted}")
  println(s"f.value=${f.value}")
  try {
    println(s"f.value.get=${f.value.get}")
    println(s"f.value.get.get=${f.value.get.get}")
    println(s"f.value.get.get.mkString=${f.value.get.get.mkString}")
  } catch {
    case e: Exception =>
      println("The future had not completed, so attempting to get the value was hopeless.")
  }

  f onComplete {
    case Success(iterator) ⇒
      println(iterator.mkString)
      System.exit(0)

    case Failure(throwable) ⇒
      println(throwable.getMessage)
      System.exit(-1)
  }
  println("Hang on, the future probably has still not yet completed.")
  synchronized { wait() }
}
