import concurrent.Future
import concurrent.duration.Duration

object FutureWork extends App {
  val urls = List("http://scalacourses.com", "http://micronauticsresearch.com", "http://mslinn.com")
  val futures = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))

  def urlSearch(word: String, urls: List[String]): Unit = {
    val futures: List[Future[String]] = urls.map(u => Future(io.Source.fromURL(u).mkString))
    for {
      (url, future) <- urls zip futures
      contents <- future if contents.toLowerCase.contains(word)
      } println(url)
    }

  urlSearch("e", List("http://micronauticsresearch.com"))
  urlSearch("e", List("http://scalacourses.com"))
  urlSearch("e", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

  def slowUrlSearch(word: String, urls: List[String]): Unit = {
    for {
      url <- urls
      contents <- Future(io.Source.fromURL(url).mkString)
      if contents.toLowerCase.contains(word)
    } println(url)
  }

  slowUrlSearch("e", List("http://not_really_here.com", "http://scalacourses.com", "http://micronauticsresearch.com"))

  val containsFree: Future[List[String]] = Future.sequence(futures).collect {
    case value: List[String] ⇒ value.filter(_.toLowerCase.contains("free"))
  }

  containsFree.onComplete {
    case scala.util.Success(value) => println(value)
  }

  val urls2 = List("http://scalacourses.com", "http://not_really_here.com", "http://micronauticsresearch.com")
  val futures2 = urls2.map(u ⇒ Future(io.Source.fromURL(u).mkString))

  def urlSearch2(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    for {
      allDone ← Future.sequence(futures2)
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } println(url)
  }

  urlSearch2("free", urls2)
  urlSearch2("scala", urls2)

  def urlSearch3(word: String, urls: List[String]): Unit = {
    val futures2: List[Future[String]] = urls.map(u ⇒ Future(io.Source.fromURL(u).mkString))
    concurrent.Await.ready(Future.sequence(futures2), Duration.Inf) // block until all futures complete
    for {
      (url, future) ← urls2 zip futures2
      contents ← future if contents.toLowerCase.contains(word)
    } println(url)
  }

  urlSearch3("free", urls2)
  urlSearch3("scala", urls2)
}
