package solutions

import com.typesafe.config.{ ConfigFactory, Config }
import collection.JavaConverters._

object RichConfig extends App {
  implicit class RichConfig(underlying: Config) {
    def forEachConfig(path: String)(f: Config => Unit) {
      underlying.getConfigList(path).asScala.foreach(f)
    }

    def keys = underlying.entrySet().asScala.map(_.getKey).toSet
  }

  implicit class RichStringForConfig(underlying: String) {
    def toConf = ConfigFactory.parseString(underlying)
  }
}

object ConfigApp extends App {
}
