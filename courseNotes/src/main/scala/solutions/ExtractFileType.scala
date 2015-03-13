package solutions

object ExtractFileType extends App {
  def findExtension(s: String): Option[String] = s.reverse.split("\\.") match {
    case Array(ext, _*) if ext.nonEmpty && !s.endsWith(".") && s.contains(".") => Some(ext.reverse)
    case _ => None
  }

  println(s"""findExtension(".")=${findExtension(".")}""")
  println(s"""findExtension("html")=${findExtension("html")}""")
  println(s"""findExtension(".html")=${findExtension(".html")}""")
  println(s"""findExtension("page.html")=${findExtension("page.html")}""")
  println(s"""findExtension("page.")=${findExtension("page.")}""")
  println(s"""findExtension(""path/prefix.page.html")=${findExtension("path/prefix.page.html")}""")
}
