import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import multi.ActorExercise._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

object ActorSystemConfig {
  val configApplication: Config = ConfigFactory.load("application.conf")
  val configDefault: Config     = ConfigFactory.load

  private val confStr = """
                  |""".stripMargin
  val stringConf: Config        = ConfigFactory.parseString(confStr)
  val config: Config            = ConfigFactory.load(stringConf
                                                       .withFallback(configApplication)
                                                       .withFallback(configDefault)
                                                    )
}

class ActorTest extends TestKit(ActorSystem("test", ActorSystemConfig.config))  // the order of these mixins is significant
  with AnyWordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll {

  val chunkerMsg1: ChunkerMsg = ChunkerMsg(10, 10, "Ain't this grand?")
  val workerMsg1: WorkerMsg = WorkerMsg(10, 10)
  val persistenceMsg: PersistenceMsg = PersistenceMsg(10, "Bloop")

  val chunker1ActorRef: TestActorRef[Chunker] = TestActorRef[Chunker]("chunker1")
  val chunker1Actor: Chunker = chunker1ActorRef.underlyingActor

  val worker1ActorRef: TestActorRef[Worker] = TestActorRef(Props[Worker](), chunker1ActorRef, "worker1")
  val worker1Actor: Worker = worker1ActorRef.underlyingActor

  val persistenceActorRef: TestActorRef[Persistence] = TestActorRef(Props[Persistence](), chunker1ActorRef, "persistence")
  val persistenceActor: Persistence = persistenceActorRef.underlyingActor

  "ActorPaths" should {
    "behave" in {
      assertResult("akka://test/user/chunker1",             "chunker1 path")   (chunker1ActorRef.path.toString)
      assertResult("akka://test/user/chunker1/worker1",     "worker1 path")    (worker1ActorRef.path.toString)
      assertResult("akka://test/user/chunker1/persistence", "persistence path")(persistenceActorRef.path.toString)
    }
  }

  override def afterAll(): Unit = {
    println("Shutting down ActorSystem")
    TestKit.shutdownActorSystem(system)
  }
}
