object LoanPattern extends App {
  import java.io._
  import scala.util.{Failure, Success, Try}

  def withCloseable[C <: Closeable, T](factory: ⇒ C)(operation: C ⇒ T): Try[T] = {
    val closeable = factory
    try {
      val result: T = operation(closeable)
      closeable.close()
      Success(result)
    } catch {
      case throwable: Throwable ⇒
        try { closeable.close() } catch { case _: Throwable ⇒ }
        Failure(throwable)
    }
  }

  def read(inputStream: InputStream): String =
    Stream.continually(inputStream.read).takeWhile(_ != -1).map(_.toChar).mkString

  def print2lines(msg: String, string: String): Unit = println(s"$msg:\n  " + string.split("\n").take(2).mkString("\n  "))

  val tryContents: Try[String] = withCloseable(new FileInputStream(new File("/etc/passwd")))(read)
  tryContents.foreach(print2lines("tryContents", _))

  val openFileInputStream2 = withCloseable(new FileInputStream(new File("/etc/passwd")))(_: FileInputStream => String)
  val tryContents2: Try[String] = openFileInputStream2 { read }
  tryContents2.foreach(print2lines("tryContents2", _))

  val fileInputStream = new FileInputStream(new File("/etc/passwd"))

  // underscore is optional if a type hint on the left of the equals sign indicates currying
  val openFileInputStream3: ((FileInputStream) => String) => Try[String] = withCloseable(fileInputStream) _
  val tryContents3: Try[String] = openFileInputStream3 { read }
  tryContents3.foreach(print2lines("tryContents3", _))

  // underscore is optional if a type hint on the left of the equals sign indicates currying
  val openFileInputStream4: ((FileInputStream) => String) => Try[String] = withCloseable(fileInputStream)
  val tryContents4: Try[String] = openFileInputStream4 { read }
  tryContents4.foreach(print2lines("tryContents4", _))

  def openFileInputStream5[T] = withCloseable[FileInputStream, T](fileInputStream) _
  val tryContents5: Try[String] = openFileInputStream5 { read }
  tryContents5.foreach(print2lines("tryContents5", _))
}
