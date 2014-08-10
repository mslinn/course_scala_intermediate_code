object PartiallyApplliedUsing {
  import java.io._
  import scala.util.{Failure, Success, Try}
  //
  val file = new File("/etc/passwd")
  def fileInputStream = new FileInputStream(file)
  //
  def read(inputStream: InputStream): String =
    Iterator.continually(inputStream.read).takeWhile(_ != -1).map(_.toChar).mkString
  //
  def first2lines(msg: String, inputStream: InputStream): String = {
    val string = read(inputStream)
    s"$msg\n " + string.split("\n").take(2).mkString("\n  ")
  }
  //
  def withT[T, U](t: T)(operation: T => U): U = operation(t)
  withT(new java.util.Date) { println }
  //
  def withCloseable[C <: Closeable, T](factory: => C)(operation: C ⇒ T): Try[T] = {
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
  //
  withCloseable(fileInputStream)(fis ⇒ first2lines("withCloseable example 1", fis))
  withCloseable(fileInputStream)(first2lines("withCloseable example 2", _))
  //
  val tryContents1: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents1", _) }
  val tryContents2: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents2", _).toUpperCase }
  //
  val openFileInputStream3 = withCloseable(fileInputStream)(_: FileInputStream => String)
  val tryContents3: Try[String] = openFileInputStream3 { first2lines("tryContents3", _).reverse }
  //
  val openFileInputStream4: (FileInputStream => String) => Try[String] = withCloseable(fileInputStream)
  val tryContents4: Try[String] = openFileInputStream4(first2lines("tryContents4", _).toUpperCase.reverse)
  //
  val noGood = withCloseable(fileInputStream) _
  //
  val openFileInputStream5 = withCloseable[FileInputStream, String](fileInputStream) _
  val tryContents5: Try[String] = openFileInputStream5(first2lines("tryContents5", _).toUpperCase)
  //
  def openFileInputStream6[T] = withCloseable[FileInputStream, T](fileInputStream) _
  val tryContents6: Try[String] = openFileInputStream6 { first2lines("tryContents6", _).replace(":", "#") }
  //
  def withBufferedInputStream1[T](input: File) =
    withCloseable(new BufferedInputStream(new FileInputStream(input))) (_: BufferedInputStream => T)
  //
  def withBufferedInputStream[T](input: File): (BufferedInputStream => T) => Try[T] =
    withCloseable(new BufferedInputStream(new FileInputStream(input)))
  //
  withBufferedInputStream(file) { first2lines("withBufferedInputStream", _) } foreach println
  //
  def withBufferedOutputStream[T](input: File): (BufferedOutputStream ⇒ T) ⇒ Try[T] =
    withCloseable(new BufferedOutputStream(new FileOutputStream(input)))
  //
  withBufferedInputStream(file) { inputStream ⇒
    withBufferedOutputStream(new File("/tmp/blah")) { outputStream ⇒
      read(inputStream).foreach(outputStream.write(_))
    }
  }
  //
  def stringMunger(a: String)(f: String ⇒ String) = f(a)
  val abc = stringMunger("abc") _
  abc { x => x * 4 }
  abc { _ * 4 }
  //
  def repeat3(a: String)(b: String)(f: (String, String) ⇒ String) = f(a, b)
  val abcdef = repeat3("abc")("def") _
  abcdef { (s1, s2) ⇒ s1 + s2 }
  def repeat3b(a: String)(b: String)(f: (String, String) ⇒ String) = a + b + f(a, b)
  val r3b = repeat3b("x")("y") _
  r3b { (u, v) ⇒ (u.length + v.length).toString }
  //
}
