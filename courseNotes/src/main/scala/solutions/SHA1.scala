package solutions

import java.security.MessageDigest
import java.io.{FileInputStream, InputStream}

object SHA1 extends App {
  def digest(fileName: String, algorithm: String): Array[Byte] = {
    val inStream = new FileInputStream(fileName)
    digest(inStream, algorithm)
  }

  def digest(inStream: InputStream, algorithm: String="SHA-256"): Array[Byte] = {
    val md = MessageDigest.getInstance(algorithm)
    val buffer = new Array[Byte](1024)
    Iterator.continually { inStream.read(buffer) }
      .takeWhile(_ != -1)
      .foreach { md.update(buffer, 0, _) }
    md.digest
  }

  println(s"""SHA-1 of /etc/passwd is ${digest("/etc/passwd", "SHA-1").mkString}""")
}
