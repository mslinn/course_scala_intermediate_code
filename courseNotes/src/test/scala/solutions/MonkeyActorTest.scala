package solutions

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

object ActorSystemConfig {
  private val confStr =  """akka {
                   |  # change to "DEBUG" to see more output
                   |  loglevel = "DEBUG"
                   |  actor {
                   |    debug {
                   |      # enable function of LoggingReceive, which is to log any received message at DEBUG level
                   |      receive = on
                   |
                   |      # enable DEBUG logging of actor lifecycle changes
                   |      lifecycle = on
                   |    }
                   |  }
                   |}""".stripMargin
  val config: Config = ConfigFactory.parseString(confStr).withFallback(ConfigFactory.load())
}

class MonkeyActorTest extends TestKit(ActorSystem("test", ActorSystemConfig.config)) // the order of these mixins is significant
  with AnyWordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll {

  val targetString = "abc"
  val alphabet = "abcdef"
  val startMsg: BookkeeperStart = BookkeeperStart(targetString, alphabet, 10)

  val monkeyActorRef: TestActorRef[Monkey] = TestActorRef(new Monkey(targetString.length, alphabet, _ => ""))
  val monkeyActor: Monkey = monkeyActorRef.underlyingActor

  val bookKeeperActorRef: TestActorRef[BookKeeper] = TestActorRef(new BookKeeper(1, false))
  val BookKeeperActor: BookKeeper = bookKeeperActorRef.underlyingActor

  "Monkeys" should {
    "generate proper strings" in {
      val string = monkeyActor.randomString
      string.length === targetString.length
    }
  }

  "BookKeepers" should {
    "compute longestStr properly" in {
      assert("abcdef" === BookKeeperActor.longestStr("abc", "abcdef"))
      assert("abcdef" === BookKeeperActor.longestStr("", "abcdef"))
      assert("abc" === BookKeeperActor.longestStr("abc", ""))
    }

    "compute matchSubstring properly" in {
      assert("abc" === BookKeeperActor.matchSubstring("abc", "abcdef"))
      assert("" === BookKeeperActor.matchSubstring("", "abcdef"))
      assert( "abc" === BookKeeperActor.matchSubstring("abcz", "abcdef"))
      assert("abc" === BookKeeperActor.matchSubstring("abcdef", "abc"))
    }
  }

  override def afterAll(): Unit = {
    println("Shutting down ActorSystem")
    TestKit.shutdownActorSystem(system)
  }
}
