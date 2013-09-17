import java.io.File
import java.net.URL
import scala.sys.process._

object ImplicitFun extends App {

  /** Enhance java.io.File */
  implicit class RichFile(file: File) {
    val bis = new java.io.BufferedInputStream(new java.io.FileInputStream(fileName))
    val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    bis.close()

    /** If file does not exist, return None.
      * Else if file is a directory, return Option[List[File]], else return contents as Option[Array[Byte]] */
    def contents: Option[Either[Array[Byte], List[File]]] = ???

    /** If file does not exist, or cannot be represented as a String, return None.
      * Else return contents as a String */
    def contentsAsString: Option[String] = ???

    /** Copy file to newFile; return true if successful */
    def copy(newFile: File): Boolean = ???

    /** Copy file to newFileName; return true if successful */
    def copy(newFileName: String): Boolean = ???
  }

  val fileName = "scalacourses.html"
  val file = new File(fileName)
  new URL("http://scalacourses.com") #> file !

  val contents = file.contentsAsString
  file.copy("copy_" + fileName)
}
