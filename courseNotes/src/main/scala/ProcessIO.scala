import scala.sys.process._

object ProcessIO extends App {
  val passwds = "cat /etc/passwd" !!;
  println(s"passwds=$passwds")

  val fileNames = "ls" !!;
  println(s"fileNames=$fileNames")

  val grepX = "printf xray\\nyankee\\nzulu" #> "grep x" !!;
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

object ComposedPBuilders extends App {
  import java.io.File
  import java.net.URL
  import sys.process._

  def readUrlPBuilder(url: String, fileName: String): ProcessBuilder = new URL(url) #> new File(fileName) cat

  readUrlPBuilder("http://scalacourses.com", "scalaCourses.html") !;
  println("cat scalaCourses.html" !)

  try {
    readUrlPBuilder("http://n_o_s_u_c_h.com", "nosuch.html") !
  } catch {
    case e: Exception => println(e.getMessage)
  }

  val yes = readUrlPBuilder("http://scalacourses.com", "scalacourses.html") #&& "echo yes" !!;
  println(s"yes=$yes")

  val no = readUrlPBuilder("http://n_o_s_u_c_h.com", "nosuch.html") #|| "echo no" !!;
  println(s"no=$no")
}
