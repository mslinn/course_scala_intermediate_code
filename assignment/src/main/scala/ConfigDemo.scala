import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConverters._

object ConfigDemo extends App {
  implicit class ConfigHelper(config: Config) {
    def keys: Set[String] = config.entrySet.asScala.map(_.getKey).toSet

    def getInt(key: String, defaultValue: Int): Int =
      if (keys contains key) config.getInt(key) else defaultValue

    def getString(key: String, defaultValue: String): String =
      if (keys contains key) config.getString(key) else defaultValue
  }

  implicit class ConfigHelper2(string: String) {
    def toConf: Config = ConfigFactory.parseString(string)
  }

  val config: Config = """
                         |keyName1 : "Value 1"
                         |keyName2 = true
                         |nested.key.name3 : 5
                         |nested { key { name4 = { "one" : 10, "two" : 12 } } }
                         |array : [ 1, 2, 3]
                         |""".stripMargin.toConf
  println(s"Keys: ${config.keys}")
  println(s"""getString("keyName1"): ${config.getString("keyName1", "defaultValue")}""")
  println(s"""getString("nonExistantKey"): ${config.getString("nonExistantKey", "defaultValue")}""")
}
