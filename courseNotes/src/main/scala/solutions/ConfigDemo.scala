package solutions

import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConverters._

object ConfigDemo extends App {
  def showValues(configName: String, config: Config)(implicit keyList: List[String]): Unit = {
    val keySet: Set[String] = config.entrySet.asScala.map(_.getKey).toSet

    def show(key: String): Unit =
      if (keySet contains key) {
        val accessKey = config.getString(key)
        println(s"$configName defines $key=$accessKey")
      } else
        println(s"$configName does not define $key as one of its ${keyList.size} keys")

    if (keySet.isEmpty) {
      println(s"$configName is empty")
    } else {
      keyList foreach show
    }
    println()
  }

  val defaultStr = """aws {
                     |  accessKey = "stringAccessKey"
                     |  secretKey = "stringSecretKey"
                     |}""".stripMargin
  val strConf = ConfigFactory.parseString(defaultStr)
  val appConf = ConfigFactory.parseResources("application.conf")
  val libConf = ConfigFactory.parseResources("library.conf")
  val defConf = ConfigFactory.load
  val combined: Config = strConf
      .withFallback(appConf)
      .withFallback(libConf)
      .withFallback(defConf)

  def showAll(implicit keyList: List[String]): Unit = {
    showValues("defaultStr",       strConf)
    showValues("library.conf",     libConf)
    showValues("application.conf", appConf)
    showValues("Default Config",   defConf)
    showValues("Combined Config",  combined)
  }

  showAll(List("aws.accessKey", "aws.secretKey", "akka.version", "user.home"))
}
