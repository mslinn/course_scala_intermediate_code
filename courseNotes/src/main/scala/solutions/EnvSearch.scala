package solutions

import scala.jdk.CollectionConverters._

object EnvSearch extends App {
  def findEnvValuesWith(name: String): Seq[String] = {
    System.getenv
      .asScala
      .filter( _._2.toLowerCase contains name.toLowerCase )
      .values
      .toList
  }

  if (args.isEmpty)
    println("Please provide a text string to search environment values for.")
  else
  println(findEnvValuesWith(args.head).mkString("\n"))
}
