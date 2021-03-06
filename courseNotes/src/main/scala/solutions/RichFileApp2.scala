package solutions

import java.io._
import java.nio.charset.Charset
import scala.util.{Failure, Success, Try}

/** Elegant solution to http://www.scalacourses.com/lectures/admin/showLecture/16/111 */
object RichFile2 {
  import language.implicitConversions

  implicit def stringToFile(fileName: String) = new File(fileName)

  implicit class EnrichedFile(underlying: File)(implicit charset: Charset=Charset.forName("UTF-8")) {

    private def withCloseable[C <: java.io.Closeable, T](factory: => C)(operation: C => T): Try[T] = {
      val closeable = factory
      try {
        val result: T = operation(closeable)
        closeable.close()
        Success(result)
      } catch {
        case throwable: Throwable =>
          try { closeable.close() } catch { case _: Throwable => }
          Failure(throwable)
      }
    }

    private def read(inputStream: InputStream): LazyList[Int] =
      LazyList.continually(inputStream.read).takeWhile(_ != -1)

    private def readAsByteArray(input: InputStream): Array[Byte] =
      read(input).map(_.toByte).toArray

    /** Not required, only provided for discussion purposes */
    private def readAsString(input: InputStream): String =
      read(input).map(_.toChar).mkString

    /** @return Some(Byte array of contents), or None if underlying File is a directory or any other problem */
    def contents: Option[Either[Array[Byte], List[File]]] = {
      if (underlying.exists) {
        if (underlying.isDirectory)
          Some(Right(listFiles))
        else
          withBufferedInputStream(readAsByteArray) match {
            case Success(bytes) => Some(Left(bytes))
            case _ => None
          }
      } else
        None
    }

    /** Partially applied function; the `withCloseable` `functor` that returns Try[T] is unbound */
    def withBufferedInputStream[T]: (BufferedInputStream => T) => Try[T] =
      withCloseable(new BufferedInputStream(new FileInputStream(underlying)))

    /** Partially applied function; the `withCloseable` `functor` that returns Try[T] is unbound */
    def withBufferedOutputStream[T]: (BufferedOutputStream => T) => Try[T] =
      withCloseable(new BufferedOutputStream(new FileOutputStream(underlying)))

    /** @return list of files in the underlying directory, or the empty list of the underlying File is not a directory */
    def listFiles: List[File] =
      if (underlying.isDirectory)
        underlying.listFiles().toList
      else
        Nil

    /** Copy the underlying File to newFile.
      * @return false if the underlying File is a directory, or if there is any other problem
      * View bounds are deprecated, so implicit parameter used instead */
    def copy[T](newFile: T)
               (implicit ev: T => File): Boolean = {
      if (underlying.isDirectory) {
        false
      } else {
        withBufferedInputStream { inputStream =>
          EnrichedFile(newFile).withBufferedOutputStream { outputStream =>
            read(inputStream).foreach(outputStream.write)
            true
          }.get
        } match {
          case Success(_) => true
          case _ => false
        }
      }
    }

    /** @return Some(contents of the underlying file), or None if the underlying File is a directory, or any other problem */
    def contentsAsString(implicit charset: Charset): Option[String] = contents match {
      case Some(Left(bytes)) => Some(new String(bytes, charset))
      case _ => None
    }
  }
}

object RichFileApp2 extends App {
  import RichFile2._

  val homeDirName = System.getProperty("user.home")
  val file = createFile

  if (file.copy(new File(homeDirName, "dest.txt")))
    println("Copied file")
  else
    println("Could not copy file")

  file.deleteOnExit()

  private def createFile: File = {
    val newFile = new File(homeDirName, "test.txt")
    val writer = new PrintWriter(newFile)
    writer.write("May the fleas of a thousand camels...\n")
    writer.close()
    newFile
  }
}
