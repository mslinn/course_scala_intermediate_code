package solutions

object ExtractFileType extends App {
  def findExtension(s: String): Option[String] = s.reverse.split("\\.").toList match {
    case ext :: remainder if ext.nonEmpty => Some(ext.reverse)
    case _ => None
  }

  println(s"""findExtension("html")=${findExtension("html")}""")
  println(s"""findExtension(".html")=${findExtension(".html")}""")
  println(s"""findExtension("page.html")=${findExtension("page.html")}""")
  println(s"""findExtension(""path/prefix.page.html")=${findExtension("path/prefix.page.html")}""")
}
