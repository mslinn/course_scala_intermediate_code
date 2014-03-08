object IoSource extends App {
  io.Source.fromFile("/etc/passwd") foreach print

  val len = io.Source.fromFile("/etc/passwd").mkString.length
  println(s"$len characters printed from /etc/passwd")

  val rootLines = io.Source.fromFile("/etc/passwd").getLines().toList
  println(s"${rootLines.length} lines printed from /etc/passwd:\n  " + rootLines.mkString("\n  "))

  val rootLines2 = (for {
    line <- io.Source.fromFile("/etc/passwd").getLines()
  } yield line).toList
  println(s"${rootLines2.length} lines printed from /etc/passwd:\n  " + rootLines2.mkString("\n  "))
}

object Writing extends App {
  import java.io.{File, PrintWriter}

  val writer = new PrintWriter(new File("test.txt"))
  writer.write("Hello Scala")
  writer.close()
}
