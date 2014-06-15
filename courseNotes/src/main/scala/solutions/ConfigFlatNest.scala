package solutions

import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConverters._
import java.io.{File, PrintWriter}

object ConfigFlatNest extends App {
  def flatKeyValueTuples(config: Config): List[(String, Any)] = {
    for {
      entry <- config.entrySet.asScala.toList
    } yield (entry.getKey, entry.getValue.render)
  }

  val configApp = ConfigFactory.load
  val tuples = flatKeyValueTuples(configApp).sortBy(_._1)
  val result1 = tuples.map( x => s"${x._1} = ${x._2}").mkString("", "\n", "\n")

  val writer = new PrintWriter(new File(System.getProperty("user.home"), "application.conf"))
  writer.write(result1)
  writer.close()
}
