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

object BinaryIO extends App {
  val scalaCompilerPath: String = {
    import sys.process._
    ("which scalac" !!).trim
  }

  val compilerObject1 = io.Source.fromFile(scalaCompilerPath, "ISO-8859-1").map(_.toByte).toArray

  val compilerObject2 = scala.io.Source.fromFile(scalaCompilerPath, "ISO-8859-1")
  val compilerAsByteArray = compilerObject2.map(_.toByte).toArray
  compilerObject2.close()

  val compilerAsByteArray2 = try { compilerObject2.map(_.toByte).toArray } finally { compilerObject2.close() }

  val bis = new java.io.BufferedInputStream(new java.io.FileInputStream(scalaCompilerPath))
  val compilerAsByteArray3 = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
  bis.close()
}
