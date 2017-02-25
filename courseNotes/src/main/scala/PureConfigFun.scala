import java.nio.file.{Path, Paths}
import PureConfigFun._
import pureconfig.error.ConfigReaderFailures

object PureConfigTest extends App {
  val pureConfigFun = PureConfigFun.load
  println(pureConfigFun)
}

object PureConfigTest2 extends App {
  val pureConfigFun = PureConfigFun.loadOrThrow
  println(pureConfigFun)
}

object PureConfigFun {
  def load: Either[ConfigReaderFailures, PureConfigFun] = pureconfig.loadConfig[PureConfigFun](Paths.get("pure.conf"))
  def loadOrThrow: PureConfigFun = pureconfig.loadConfigOrThrow[PureConfigFun](Paths.get("pure.conf"))

  def apply: PureConfigFun = loadOrThrow

  val defaultConsoleConfig   = ConsoleConfig()
  val defaultFeedConfig      = FeedConfig()
  val defaultReplConfig      = ReplConfig()
  val defaultSpeciesConfig   = SpeciesConfig()
  val defaultSshServerConfig = SshServerConfig()
}

case class PureConfigFun(
  console: ConsoleConfig         = defaultConsoleConfig,
  feed: FeedConfig               = defaultFeedConfig,
  repl: ReplConfig               = defaultReplConfig,
  speciesDefaults: SpeciesConfig = defaultSpeciesConfig,
  sshServer: SshServerConfig     = defaultSshServerConfig
)

case class FeedConfig(port: Port = Port(1100))

case class ConsoleConfig(enabled: Boolean = true) extends AnyVal

case class Port(value: Int) extends AnyVal

case class ReplConfig(
  home: Path = Paths.get(System.getProperty("user.home"))
) extends AnyVal

case class SpeciesConfig(
  attributeMinimum: Int = 0,
  attributeMaximum: Int = 100,
  eventQLength: Int = 25,
  historyLength: Int = 20
)

case class SshServerConfig(
  enabled: Boolean = true,
  password: String = "",
  port: Port = Port(1101)
)
