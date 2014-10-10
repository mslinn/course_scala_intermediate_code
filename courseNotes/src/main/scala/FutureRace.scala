object FutureRace extends App {
  import java.util.concurrent.{Executors, ExecutorService}
  import scala.concurrent.{ExecutionContext, Await, Future}
  import scala.util.{Success, Failure}
  import scala.concurrent.duration.Duration

  val pool: ExecutorService = Executors.newFixedThreadPool(1)
  implicit val ec = ExecutionContext.fromExecutor(pool)

  @volatile var offset = 6 // @volatile does not solve race conditions
  def accessor = offset

  val f1 = Future {
    2 + 3 + offset // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Race Scala result1 : " + result)
    case Failure(exception) => println("Race Scala exception 1: " + exception.getMessage)
  }

  val f2 = Future {
    2 + 3 + accessor // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Race Scala result 2: " + result)
    case Failure(exception) => println("Race Scala exception 2: " + exception.getMessage)
  }
  offset = 42
  println("End of mainline, offset = " + offset)

  Await.ready(Future.sequence(List(f1, f2)), Duration.Inf)
}
