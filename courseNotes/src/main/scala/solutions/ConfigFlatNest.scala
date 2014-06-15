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

  val file = new File(System.getProperty("user.home"), "application.conf")
  val writer = new PrintWriter(file)
  writer.write(result1)
  writer.close()

  val originalEntrySet = configApp.entrySet
  val x = configApp.getAnyRef("akka.io.tcp.finish-connect-retries")
  val confFlat = ConfigFactory.parseFile(file)
  val matched = confFlat.entrySet.asScala.forall { kv =>
    val flatKey = kv.getKey
    val flatValue = kv.getValue.render
    val originalValue = configApp.getAnyRef(flatKey)
    if (originalValue != flatValue)
      println(s"$flatKey: $originalValue (${originalValue.getClass.getName}) != $flatValue  (${flatValue.getClass.getName})")
    originalValue == flatValue
  }
  if (matched)
    println(s"Flat version in ${file.getAbsolutePath} matches original hierarchical version")
  else
    println(s"Flat version in ${file.getAbsolutePath} does not match original hierarchical version")
}
