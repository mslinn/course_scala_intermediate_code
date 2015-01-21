package multi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

package object futures {
  lazy val badHostFuture: Future[String]     = readUrlFuture(badHostUrlStr)
  lazy val badPageFuture: Future[String]     = readUrlFuture(badHostUrlStr)
  lazy val badProtocolFuture: Future[String] = readUrlFuture(badHostUrlStr)
  lazy val defaultFuture: Future[String]     = readUrlFuture(goodUrlStr1)

  /** @return Future of first maxChars characters of web page at given url */
  def readUrlFuture(urlStr: String, maxChars: Int=500): Future[String] = Future(readUrl(urlStr, maxChars))
}
