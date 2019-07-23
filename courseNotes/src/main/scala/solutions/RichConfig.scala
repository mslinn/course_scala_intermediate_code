package solutions

import com.typesafe.config.{ ConfigFactory, Config }
import scala.jdk.CollectionConverters._
import scala.collection._

/** Solution to http://www.scalacourses.com/lectures/admin/showLecture/18/96 */
object RichConfig extends App {
  implicit class RichConfig(underlying: Config) {
    def forEachConfig(path: String)(f: Config => Unit): Unit =
      underlying
        .getConfigList(path)
        .asScala
        .foreach(f)

    def keys: immutable.Set[String] =
      underlying
        .entrySet
        .asScala
        .map(_.getKey)
        .toSet
  }

  implicit class RichStringForConfig(underlying: String) {
    def toConf: Config = ConfigFactory.parseString(underlying)
  }
}
