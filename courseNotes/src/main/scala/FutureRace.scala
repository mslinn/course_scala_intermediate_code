object FutureRace extends App {
  import java.util.concurrent.{Executors, ExecutorService}
  import scala.concurrent.{ExecutionContext, Await, Future}
  import scala.util.{Success, Failure}
  import scala.concurrent.duration.Duration

  val threadCount: Int = try {
      if (args.length==1) math.max(1, args(0).toInt) else 1
  } catch {
    case e: Exception =>
      println("You can specify the number of threads in the threadpool on the command line; default value is 1")
      System.exit(-1)
      1
  }

  val pool: ExecutorService = Executors.newFixedThreadPool(threadCount)
  implicit val ec = ExecutionContext.fromExecutor(pool)

  @volatile var offset = 6 // @volatile does not affect race conditions
  def accessor = offset

  val f1 = Future {
    2 + 3 + offset // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Race Scala result 1 : " + result)
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
  pool.shutdown()
}
