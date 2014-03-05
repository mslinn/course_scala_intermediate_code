import scala.sys.process._

object ProcessIO extends App {
  val passwds = "cat /etc/passwd" !!;
  println(s"passwds=$passwds")

  val fileNames = "ls" !!;
  println(s"fileNames=$fileNames")

  val grepX = "printf %s xray\\nyankee\\nzulu" #> "grep x" !!;
  println(s"grepX=$grepX")

}

object URLBuilderDemo extends App {
  import java.io.File
  import java.net.URL
  import sys.process._

  new URL("http://scalacourses.com") #> new File("scalaCourses.html") !;
  println("cat scalaCourses.html" !)
}

object FileBuilderDemo extends App {
  import java.io.File
  import sys.process._

  "ls" #> new File("dirContents.txt") !;
  println("cat dirContents.txt" !)
}
