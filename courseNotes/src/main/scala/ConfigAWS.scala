import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConverters._

object ConfigAWS extends App {
  def showValues(configName: String, config: Config)(implicit keyList: List[String]): Unit = {
    val keySet: Set[String] = config.entrySet.asScala.map(_.getKey).toSet

    def show(key: String): Unit =
      if (keySet contains key) {
        val accessKey = config.getString(key)
        println(s"$configName defines $key=$accessKey")
      } else
        println(s"$configName does not define $key as one of its ${ keyList.size } keys")

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
  val strConf = ConfigFactory.parseString(defaultStr).resolve
  val appConf = ConfigFactory.parseResources("application.conf").resolve
  val libConf = ConfigFactory.parseResources("library.conf").resolve
  val defConf = ConfigFactory.load
  val combined: Config =
    strConf                   // highest priority
      .withFallback(appConf)  // second highest priority
      .withFallback(libConf)  // priority 3
      .withFallback(defConf)  // lowest priority

  def showAll(implicit keyList: List[String]): Unit = {
    showValues("defaultStr",       strConf)
    showValues("library.conf",     libConf)
    showValues("application.conf", appConf)
    showValues("Default Config",   defConf)
    showValues("Combined Config",  combined)
  }

  showAll(List("aws.accessKey", "aws.secretKey", "akka.version", "user.home"))
}
