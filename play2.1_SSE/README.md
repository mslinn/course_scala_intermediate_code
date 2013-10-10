# Play 2.1 Server-Sent Events #

This is a Play 2.1 application that demonstrates how Server-Sent Events (SSE) are published from Play server to HTML5 client.

The published objects are case class instances:

    case class HomeworkUpload(name: String, lectureId: Long, studentUserId: Long)

The case classes are converted to JSON by an implicit:

    private implicit val hwWrites = Json.writes[HomeworkUpload]

The publishing channel and enumerator is defined when the application starts.
Note that the channel publishes JSON (expressed as JsValue):

    private val (enumerator, channel) = Concurrent.broadcast[JsValue]

The browser loads the HTML index page like this:

    def index = Action {
      Ok(views.html.index())
    }

The data source is a Java timer that creates `homework` objects and publishes them by pushing them through the `channel`:

    private var count = 1L

    TimedTask(1) {
      val homework = HomeworkUpload(s"fileName$count", 123, 456)
      notifyHomeworkListeners(homework)
      count = count + 1
    }

    def notifyHomeworkListeners(homework: HomeworkUpload) = {
      channel.push(Json.toJson(homework))
    }

`TimedTask` is defined like this:

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

The route for subscribing to the stream of homework objects is defined like this:

    GET  /subscribeToHomeworkStream  controllers.Application.subscribeToHomeworkStream

Browsers subscribe to the stream of homework events by invoking this Play Action:

    def subscribeToHomeworkStream() = Action { implicit request =>
      Ok.feed(enumerator &> EventSource()).as("text/event-stream")
    }

The Play Action is invoked from JavaScript like this:

    var feed = new EventSource('@routes.Application.subscribeToHomeworkStream');

Each homework object that is received by a browser is appended to this HTML list, using JQuery:

    &lt;ul id="eventList">&lt;/ul>

The three handlers for SSE open, message and close events look like this:

    feed.addEventListener('open', function (e) {
      console.log("Connection open");
    }, false);

    feed.addEventListener('message', function(e) {
      var data = JSON.parse(e.data);
      // do something with the message
      $("#eventList").append("<li><code>" + data.name + ", " + data.lectureId + ", " + data.studentUserId + "</code></li>");
      console.log(data);
    }, false);

    feed.addEventListener('error', function (e) {
      if (e.eventPhase == EventSource.CLOSED) {
        console.log("Connection closed");
        feed.close();
      } else {
        console.log("Unknown error" + e);
      }
    }, false);

Any other data source could be used in place of the `TimedTask`.
