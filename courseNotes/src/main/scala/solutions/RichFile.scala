package solutions

import io.Source
import java.io._

/** Non-elegant solution to http://www.scalacourses.com/lectures/admin/showLecture/16/111 */
object RichFile {
  implicit class EnrichedFile(file: File) {
    /** @return None if the File does not exist. If file is a directory,
      *  return the listing as a [List[File]] on the left side of the Either,
      *  otherwise return the contents of the File as Array[Byte]] */
    def contents: Option[Either[Array[Byte], List[File]]] =
      if (!file.exists) None
      else if (file.isDirectory) Some(Right(file.listFiles().toList))
      else try {
        val inStream = new FileInputStream(file)
        try {
          Some(Left(readAsByteArray(inStream)))
        } finally {
          if (inStream!=null) inStream.close()
        }
      } catch {
        case _: Throwable => None
      }

    /** @return directory listing of the File. If the File does not exist,
      *  or it is not a directory return an empty list. */
    def directory: List[File] =
      if (file.exists && file.isDirectory) file.listFiles.toList
      else Nil

    /** @return contents of the File as Option[String]. If the File does not exist,
      *  or it is a directory, or cannot be represented as a String, return None. */
    def contentsAsString: Option[String] =
      if (!file.exists || file.isDirectory) None
      else {
        val source = Source.fromFile(file)
        try {
          Some(source.mkString)
        } catch {
          case _ : Throwable => None
        } finally {
          source.close()
        }
      }

    private def read(inputStream: InputStream): Stream[Int] =
      Stream.continually(inputStream.read).takeWhile(_ != -1)

    private def readAsByteArray(input: InputStream): Array[Byte] =
      read(input).map(_.toByte).toArray

    /** Copy file to newFile; return true if successful.  */
    def copy[T <% File](newFile: T): Boolean =
      if (!file.exists || file.isDirectory) {
        false
      } else try {
        val inStream: FileInputStream = try {
          val is = new FileInputStream(file)
          val contents = readAsByteArray(is)
          val outStream: FileOutputStream = try {
            val os = new FileOutputStream(newFile)
            os.write(contents, 0, contents.length)
            os
          } catch { case _: Throwable =>
            null
          }
          if (outStream!=null) outStream.close()
          is
        } catch { case _: Throwable =>
          null
        }
        if (inStream!=null) inStream.close()
        true
      } catch {
        case _ : Throwable => false
      }
  }

  implicit def string2file(fileName: String): File = new File(fileName)
}

object RichFileApp extends App {
  import RichFile._

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
