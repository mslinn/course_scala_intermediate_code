package controllers

import play.api._
import libs.EventSource
import play.api.libs.iteratee.Concurrent
import libs.json.{Json, JsValue}
import play.api.mvc._

case class HomeworkUpload(name: String, lectureId: Long, studentUserId: Long)

object Application extends Controller {
  private implicit val hwWrites = Json.writes[HomeworkUpload]

  private val (enumerator, channel) = Concurrent.broadcast[JsValue]

  private var count = 1L

  TimedTask(1) {
    val homework = HomeworkUpload(s"fileName$count", 123, 456)
    notifyHomeworkListeners(homework)
    count = count + 1L
  }

  def index = Action {
    Ok(views.html.index())
  }

  def notifyHomeworkListeners(homework: HomeworkUpload) = {
    channel.push(Json.toJson(homework))
  }

  /** Subscribe to the stream of server-sent events */
  def subscribeToHomeworkStream() = Action { implicit request =>
    Ok.feed(enumerator &> EventSource()).as("text/event-stream")
  }
}

object TimedTask {
  import java.util.Timer
  import java.util.TimerTask

  def apply(intervalSeconds: Int=1)(op: => Unit) {
    val task = new TimerTask {
      def run = op
    }
    val timer = new Timer
    timer.schedule(task, 0L, intervalSeconds*1000L)
  }
}
