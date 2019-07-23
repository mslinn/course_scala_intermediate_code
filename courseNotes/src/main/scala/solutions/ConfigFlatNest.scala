package solutions

import com.typesafe.config._
import scala.jdk.CollectionConverters._
import java.io.{File, PrintWriter}

object ConfigFlatNest extends App {
  def flatKeyValueTuples(config: Config): List[(String, Any)] =
    for {
      entry <- config.entrySet.asScala.toList
    } yield (entry.getKey, entry.getValue.render)

  val renderOptions = ConfigRenderOptions.concise.setComments(false)
  val configApp = ConfigFactory.load
  val tuples = flatKeyValueTuples(configApp).sortBy(_._1)
  val result1 = tuples.map( x => s"${x._1} = ${x._2}").mkString("", "\n", "\n")

  val file = new File(System.getProperty("user.home"), "application.conf")
  val writer = new PrintWriter(file)
  writer.write(result1)
  writer.close()

  val originalEntrySet = configApp.entrySet
  val confFlat = ConfigFactory.parseFile(file)
  val matched = confFlat.entrySet.asScala.forall { kv =>
    def removeOuterParens(string: String): String =
      if (string.startsWith("\"") && string.endsWith("\"")) string.substring(1, string.length-1) else string

    def render(value: ConfigValue): String =
      value.unwrapped match {
        case l: List[_] =>
          l.map { case item: ConfigValue => render(item) }.mkString(", ")

        case configString: String =>
          removeOuterParens(value.render(renderOptions))

        case _ =>
          value.render(renderOptions)
      }

    def renderObject(value: Object): String =
      value match {
        case jList: java.util.List[_] =>
          jList.asScala.map { case obj: Object => renderObject(obj) } .mkString(", ")

        case string: String =>
          removeOuterParens(string)

        case _ =>
          value.toString
      }

    val flatKey: String = kv.getKey
    val flatValue: String = kv.getValue match {
      case list: ConfigList =>
        list.unwrapped.asScala.map { renderObject }.mkString("[", ", ", "]")

      case value =>
        removeOuterParens(value.render(renderOptions))
    }
    val originalValue: String = configApp.getAnyRef(flatKey).toString.replace("\n", "\\n")
    val comparesEqual: Boolean = originalValue.toString == flatValue
    if (!comparesEqual)
      println(s"$flatKey: $originalValue (${originalValue.getClass.getName}) != $flatValue (${flatValue.getClass.getName})")
    comparesEqual
  }

  println(s"""Flat version in ${file.getAbsolutePath} ${ if (matched) "matches" else "does not match" } original hierarchical version""")
}
