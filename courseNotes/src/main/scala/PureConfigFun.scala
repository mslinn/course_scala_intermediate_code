import java.io.File
import java.nio.file.{Path, Paths}
import PureConfigFun._
import com.typesafe.config.ConfigValueType._
import pureconfig.error.WrongType
import pureconfig.generic.ProductHint
import scala.util.{Failure, Success, Try}

object PureConfigTest extends App {
  val pureConfigFun = PureConfigFun.load
  println(pureConfigFun)
}

object PureConfigTest2 extends App {
  val pureConfigFun = PureConfigFun.loadOrThrow
  println(pureConfigFun)
}

object PureConfigFun {
  import pureconfig._
  import pureconfig.error.ConfigReaderFailures

  val defaultConsoleConfig   = ConsoleConfig()
  val defaultFeedConfig      = FeedConfig()
  val defaultReplConfig      = ReplConfig()
  val defaultSpeciesConfig   = SpeciesDefaults()
  val defaultSshServerConfig = SshServer()

  /** Define before `load` or `loadOrThrow` methods are defined so this implicit is in scope */
  implicit val readPort: ConfigReader[Port] = ConfigReader.fromCursor[Port] { cur =>
    cur.asString.right.flatMap { str =>
      Try(str.toInt) match {
        case Success(number) => Right(Port(number))

        case Failure(_) => cur.failed(WrongType(STRING, Set(NUMBER)))
      }
    }
  }

  /** Fail if an unknown key is found.
    * @see https://github.com/melrief/pureconfig/blob/master/core/docs/override-behaviour-for-case-classes.md#unknown-keys
    *
    * Support CamelCase
    * @see https://github.com/melrief/pureconfig/blob/master/core/docs/override-behaviour-for-case-classes.md#override-behaviour-for-case-classes */
  implicit val hint: ProductHint[PureConfigFun] = ProductHint[PureConfigFun](
    allowUnknownKeys = false,
    fieldMapping = ConfigFieldMapping(CamelCase, CamelCase)
  )

  val expandTilde: String => Path =
    (string: String) => Paths.get(string.replace("~", sys.props("user.home")))

  import pureconfig.ConvertHelpers._
  implicit val overridePathReader: ConfigReader[Path] =
    ConfigReader.fromString[Path](catchReadError(expandTilde))
  import pureconfig.generic.auto._

  lazy val confPath: Path = new File(getClass.getClassLoader.getResource("pure.conf").getPath).toPath

  /** Be sure to define implicits such as [[ConfigConvert]] and [[ProductHint]] subtypes before this method so they are in scope */
  def load: Either[ConfigReaderFailures, PureConfigFun] = pureconfig.loadConfigFromFiles[PureConfigFun](
    files = List(confPath),
    failOnReadError = true,
    namespace = "ew"
  )

  /** Be sure to define implicits such as [[ConfigConvert]] and [[ProductHint]] subtypes before this method so they are in scope */
  def loadOrThrow: PureConfigFun = pureconfig.loadConfigOrThrow[PureConfigFun](confPath, "ew")

  /** Be sure to define implicits such as [[ConfigConvert]] and [[ProductHint]] subtypes before this method so they are in scope */
  def apply: PureConfigFun = loadOrThrow
}

case class PureConfigFun(
  console: ConsoleConfig           = defaultConsoleConfig,
  feed: FeedConfig                 = defaultFeedConfig,
  repl: ReplConfig                 = defaultReplConfig,
  speciesDefaults: SpeciesDefaults = defaultSpeciesConfig,
  sshServer: SshServer             = defaultSshServerConfig
)

case class FeedConfig(port: Port = Port(1100))

case class ConsoleConfig(enabled: Boolean = true)/* extends AnyVal */ // value objects worked in earlier PureConfig versions

case class Port(value: Int) extends AnyVal

case class ReplConfig(
  home: Path = Paths.get(System.getProperty("user.home"))
)

case class SpeciesDefaults(
  attributeMinimum: Int = 0,
  attributeMaximum: Int = 100,
  eventQLength: Int = 25,
  historyLength: Int = 20
)

case class SshServer(
  address: String = "localhost",
  ammoniteHome: Path = Paths.get(System.getProperty("user.home") + "/.ammonite"),
  enabled: Boolean = true,
  hostKeyFile: Option[Path] = None, //Some(Paths.get(System.getProperty("user.home") + "/.ssh/id_rsa")),
  password: String = "",
  port: Port = Port(1101),
  userName: String = "repl"
)
