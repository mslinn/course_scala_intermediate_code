import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import akka.actor.ActorSystem
import org.specs2.matcher.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ActorPathTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
    with ShouldMatchers with WordSpecLike with BeforeAndAfterAll  {

  val actorPath1 = system / "path1"
  val actorPath2 = system / "path2"
  val actorPath3 = system / "path3"
  val chunkerMsg1 = ChunkerMsg(1, 1, "Hi")
  val workerMsg1 = WorkerMsg(1, 1)
  val persistenceMsg = PersistenceMsg(1, "")

  val chunker1ActorRef = TestActorRef[Chunker]("chunker1")
  val chunker1Actor = chunker1ActorRef.underlyingActor

  val worker1ActorRef = TestActorRef[Worker]("worker1")
  val worker1Actor = worker1ActorRef.underlyingActor

  val persistenceActorRef = TestActorRef[Persistence]("persistence")
  val persistenceActor = persistenceActorRef.underlyingActor

  def this() = this {
    val logLevelStr = """
                        |""".stripMargin
    val stringConf: Config = ConfigFactory.parseString(logLevelStr)
    val configApplication  = ConfigFactory.load("application.conf")
    val configDefault      = ConfigFactory.load
    val config: Config     = ConfigFactory.load(stringConf.withFallback(configApplication).withFallback(configDefault))
    ActorSystem("myApp", config)
  }

  "ActorPaths" should {
    "behave" in {
      assertResult("akka://myApp/user/chunker1",    "chunker1 path")(chunker1ActorRef.path.toString)
      assertResult("akka://myApp/user/worker1",     "worker1 path")(worker1ActorRef.path.toString)
      assertResult("akka://myApp/user/persistence", "persistence path")(persistenceActorRef.path.toString)
    }
  }

  override def afterAll(): Unit = {
    println("Shutting down ActorSystem")
    system.shutdown()
    Thread.sleep(300) // give ActorSystem time to shut down
  }
}
