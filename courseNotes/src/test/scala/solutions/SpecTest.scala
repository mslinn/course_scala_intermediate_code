package solutions

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/** Solution to http://www.scalacourses.com/lectures/admin/showLecture/15/75 */
@RunWith(classOf[JUnitRunner])
object SpecTest extends Specification {
  val ciscoURL = "https://www.cisco.com/"
  val targetWord = "cisco"

  def linesFrom(siteURL: String): Array[String] = {
    val contents = io.Source.fromURL(siteURL).mkString
    contents.toLowerCase.split("\\W+")
  }

  "Exercise 2" should {
    "Verify that cisco.com is not empty" in {
      linesFrom(ciscoURL) must not be empty
    }

    "Verify that cisco.com contains the word cisco (using iterator)" in {
      val actual = linesFrom(ciscoURL).toIterator
      actual must containMatch(targetWord)
    }

    "Verify that cisco.com contains the word cisco (using higher-order function)" in
      linesFrom(ciscoURL).contains(targetWord)
  }
}
