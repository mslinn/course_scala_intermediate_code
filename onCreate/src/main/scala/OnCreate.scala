import akka.actor.ActorSystem

case class FiletypeAction(filetype: String, commandTokens: List[String])

object OnCreate extends App {
  import java.io.File
  import java.nio.file.Path
  import java.nio.file.StandardWatchEventKinds._
  import com.beachape.filemanagement.Messages._
  import com.beachape.filemanagement.RegistryTypes._
  import com.typesafe.config.{Config, ConfigObject}
  import scala.collection.JavaConverters._
  import scala.collection.mutable

  implicit val system: ActorSystem = akka.actor.ActorSystem("actorSystem")
  val fileMonitorActor = system.actorOf(com.beachape.filemanagement.MonitorActor())

  def playSound(fileName: String): Unit = {
    try {
      import javax.sound.sampled._
      val audioIn: AudioInputStream = {
        if (fileName.startsWith("http:")) {
          AudioSystem.getAudioInputStream(new java.net.URL(fileName) )
        } else {
          val stream = getClass.getClassLoader.getResourceAsStream(fileName)
          AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(stream))
        }
      }
      val clip = AudioSystem.getClip
      clip.open(audioIn)
      clip.start()
      val promise = concurrent.Promise[String]()
      clip.addLineListener((event: LineEvent) => {
        if (event.getType == LineEvent.Type.STOP) {
          event.getLine.close()
          promise.success("done")
          ()
        }
      })
      concurrent.Await.result(promise.future, concurrent.duration.Duration.Inf)
      ()
    } catch {
      case e: Exception =>
        println(e)
    }
  }

  def handleActions(path: Path, config: Config): Unit = {
    val filetype: String = config.getString("filetype")
    if (path.toString.endsWith(filetype)) {
      val commandTokens: mutable.Buffer[String] = config.getStringList("commandTokens").asScala
      val command = commandTokens.map(_.replaceAll("\\$f", path.toString.replace("\\", "\\\\")))
      println(s"Execute: ${command.mkString(" ")}")
      playSound("ascending.wav")
      val startMillis = System.currentTimeMillis
      val result = try {
        import scala.sys.process._
        Process(command).!
      } catch {
        case e: Exception =>
          println(e.getMessage)
      }
      val endMillis = System.currentTimeMillis
      if (endMillis-startMillis<250) Thread.sleep(250)
      playSound(if (result==0) "descending.wav" else "problem.wav")
    }
  }

  val config = com.typesafe.config.ConfigFactory.parseResources("config.json")
  val createCallback: Callback = { path =>
    val am = config.getList("actionMap")
    am.asScala.foreach { case configObject: ConfigObject =>
      handleActions(path, configObject.toConfig)
    }
  }

  val watchedFile = new File(if (args.nonEmpty) args(0) else "").getCanonicalFile
  val watchedDirectory = watchedFile.getCanonicalPath
  if (!watchedFile.exists) {
    println(s"Fatal: $watchedDirectory does not exist.")
    sys.exit(-1)
  }
  println(s"Watching $watchedDirectory")

  fileMonitorActor ! RegisterCallback(
    event = ENTRY_CREATE,
    modifier = None,
    recursive = true,
    path = watchedFile.toPath,
    callback = createCallback)
}
