object IoSource extends App {
  io.Source.fromFile("/etc/passwd") foreach print

  val len = io.Source.fromFile("/etc/passwd").mkString.length
  println(s"$len characters printed from /etc/passwd")

  val rootLines = io.Source.fromFile("/etc/passwd").getLines().filter(_.contains("/var/")).toList
  println(s"${rootLines.length} lines printed from /etc/passwd containing '/var/':\n  " + rootLines.mkString("\n  "))

  val rootLines2 = (for {
    line <- io.Source.fromFile("/etc/passwd").getLines() if line.contains("/var/")
  } yield line).toList
  println(s"${rootLines2.length} lines printed from /etc/passwd containing '/var/':\n  " + rootLines2.mkString("\n  "))
}
