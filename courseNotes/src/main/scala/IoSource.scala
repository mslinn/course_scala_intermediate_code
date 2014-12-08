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

object Writing7 extends App {
  @inline def writeToTextFile(fileName: String, content: String) = {
    import java.io.{File, PrintWriter}

    val writer = new PrintWriter(new File(fileName))
    writer.write(content)
    writer.close()
  }

  writeToTextFile("test.txt", "Being disintegrated makes me go angry!")
}

object Writing8 extends App {
  import java.nio.file.{Files, Paths, StandardOpenOption}

  /** Requires Java 8 */
  @inline def writeToTextFile(fileName: String, content: String) =
    Files.write(Paths.get(fileName), content.getBytes, StandardOpenOption.CREATE)

  writeToTextFile("test.txt", "Being disintegrated makes me go angry!")
}
