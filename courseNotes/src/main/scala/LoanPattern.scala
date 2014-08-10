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
    Iterator.continually(inputStream.read).takeWhile(_ != -1).map(_.toChar).mkString

  def first2lines(msg: String, inputStream: InputStream): String = {
    val string = read(inputStream)
    s"$msg:\n  " + string.split("\n").take(2).mkString("\n  ")
  }

  val file = new File("/etc/passwd")
  def fileInputStream = new FileInputStream(file)

  val tryContents1: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents1", _) }
  tryContents1.foreach(println)

  val tryContents2: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents2", _).toUpperCase }
  tryContents2.foreach(println)

  val openFileInputStream3 = withCloseable(fileInputStream)(_: FileInputStream => String)
  val tryContents3: Try[String] = openFileInputStream3 { first2lines("tryContents3", _).reverse }
  tryContents3.foreach(println)

  val openFileInputStream4: ((FileInputStream) => String) => Try[String] = withCloseable(fileInputStream)
  val tryContents4: Try[String] = openFileInputStream4 { first2lines("tryContents4", _).toUpperCase.reverse }
  tryContents4.foreach(println)

  def openFileInputStream5[T] = withCloseable[FileInputStream, T](fileInputStream) _
  val tryContents5: Try[String] = openFileInputStream5 { first2lines("tryContents6", _).replace(":", "#") }
  tryContents5.foreach(println)

  def withBufferedInputStream1[T](input: File) =
    withCloseable(new BufferedInputStream(new FileInputStream(input))) _

  def withBufferedInputStream[T](input: File): (BufferedInputStream => T) => Try[T] =
    withCloseable(new BufferedInputStream(new FileInputStream(input)))

  withBufferedInputStream(file) { first2lines("withBufferedInputStream", _) } foreach println

  def withBufferedOutputStream[T](input: File): (BufferedOutputStream ⇒ T) ⇒ Try[T] =
    withCloseable(new BufferedOutputStream(new FileOutputStream(input)))

  withBufferedInputStream(file) { inputStream ⇒
    withBufferedOutputStream(new File("/tmp/blah")) { outputStream ⇒
      read(inputStream).foreach(outputStream.write(_))
    }
  }
}
