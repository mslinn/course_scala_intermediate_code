import akka.dataflow._
import scala.concurrent.{ExecutionContext, Promise}
import java.util.concurrent.{Executors, ExecutorService}

object DataflowFun extends App {
  val pool: ExecutorService = Executors.newFixedThreadPool(8)
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(pool)

  val x, y, z = Promise[Int]()

  flow { // new thread
    z << x() + y()
    println("z = " + z())
  }

  flow { // new thread
    x << 40
    x() // return value
  } onComplete { _.map { x => println(s"x=${x}") } }

  flow { // new thread
    y << 2
    y()
  } onComplete { y => println(s"y=${y}") }
}
