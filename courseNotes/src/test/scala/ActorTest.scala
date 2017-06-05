import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import akka.actor.{ActorSystem, Props}
import org.specs2.matcher.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
class ActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with ShouldMatchers with WordSpecLike with BeforeAndAfterAll {
  import multi._
  import ActorExercise._

  val chunkerMsg1 = ChunkerMsg(10, 10, "Ain't this grand?")
  val workerMsg1 = WorkerMsg(10, 10)
  val persistenceMsg = PersistenceMsg(10, "Bloop")

  val chunker1ActorRef: TestActorRef[Chunker] = TestActorRef[Chunker]("chunker1")
  val chunker1Actor: Chunker = chunker1ActorRef.underlyingActor

  val worker1ActorRef: TestActorRef[Worker] = TestActorRef(Props[Worker], chunker1ActorRef, "worker1")
  val worker1Actor: Worker = worker1ActorRef.underlyingActor

  val persistenceActorRef: TestActorRef[Persistence] = TestActorRef(Props[Persistence], chunker1ActorRef, "persistence")
  val persistenceActor: Persistence = persistenceActorRef.underlyingActor

  def this() = this {
    val confStr = """
                    |""".stripMargin
    val stringConf: Config = ConfigFactory.parseString(confStr)
    val configApplication  = ConfigFactory.load("application.conf")
    val configDefault      = ConfigFactory.load
    val config: Config     = ConfigFactory.load(stringConf.withFallback(configApplication).withFallback(configDefault))
    ActorSystem("myApp", config)
  }

  "ActorPaths" should {
    "behave" in {
      assertResult("akka://myApp/user/chunker1",             "chunker1 path")   (chunker1ActorRef.path.toString)
      assertResult("akka://myApp/user/chunker1/worker1",     "worker1 path")    (worker1ActorRef.path.toString)
      assertResult("akka://myApp/user/chunker1/persistence", "persistence path")(persistenceActorRef.path.toString)
    }
  }

  override def afterAll(): Unit = {
    println("Shutting down ActorSystem")
    system.terminate()
    Await.result(system.whenTerminated, concurrent.duration.Duration.Inf)
  }
}
