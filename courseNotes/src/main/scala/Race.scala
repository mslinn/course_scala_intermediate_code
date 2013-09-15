import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

object Race extends App {
  private val configString: String = "akka { logConfigOnStart=off }"
  private val system: ActorSystem = ActorSystem.apply("actorSystem", ConfigFactory.parseString(configString))
  implicit private var dispatcher: ExecutionContext = system.dispatcher

  @volatile var offset = 6 // @volatile makes no difference because it does not solve race conditions
  def accessor = offset

  val f1 = Future {
    2 + 3 + offset // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Race Scala result1 : " + result)
    case Failure(exception) => println("Race Scala exception 1: " + exception.asInstanceOf[Exception].getMessage)
  }

  val f2 = Future {
    2 + 3 + accessor // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Race Scala result 2: " + result)
    case Failure(exception) => println("Race Scala exception 2: " + exception.asInstanceOf[Exception].getMessage)
  }
  offset = 42
  system.log.info("End of mainline, offset = " + offset)

  Future.sequence(List(f1, f2)) andThen {
    case _ => system.shutdown() // terminates this thread, and the program if no other threads are active
  }
}
