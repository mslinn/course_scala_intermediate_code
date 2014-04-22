package solutions

import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConverters._

object ConfigDemo extends App {
  def showValues(msg: String, config: Config): Unit = {
    val keys: Set[String] = config.entrySet.asScala.map(_.getKey).toSet // mutable by default

    if (keys.contains("aws.accessKey")) {
      val accessKey = config.getString("aws.accessKey")
      println(s"$msg accessKey=$accessKey")
    } else
      println(s"$msg does not define aws.accessKey")

    if (keys.contains("aws.secretKey")) {
      val secretKey = config.getString("aws.secretKey")
      println(s"$msg secretKey=$secretKey")
    } else
      println(s"$msg does not define aws.secretKey")

    println()
  }

  val defaultStr = """aws {
                     |  accessKey = "stringAccessKey"
                     |  secretKey = "stringSecretKey"
                     |}""".stripMargin
  val strConf = ConfigFactory.parseString(defaultStr) // experiment by commenting this line out
  //val strConf = ConfigFactory.parseString("")       // ... and uncommenting this one
  val appConf = ConfigFactory.load("application.conf")
  val libConf = ConfigFactory.load("library.conf")
  val defConf = ConfigFactory.load
  val config: Config = ConfigFactory.load(
    strConf
      .withFallback(appConf)
      .withFallback(libConf)
      .withFallback(defConf))

  showValues("defaultStr", strConf)
  showValues("library.conf", libConf)
  showValues("application.conf", appConf)
  showValues("Default", defConf)
  showValues("Combined", config)
}
