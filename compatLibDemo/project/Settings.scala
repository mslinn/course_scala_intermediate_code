import java.io.{BufferedReader, FileReader}

object Settings {
  import java.io.{BufferedWriter, File, FileWriter}

  def using[A <: AutoCloseable, B](resource: A)
                                  (block: A => B): B = {
    try block(resource) finally resource.close()
  }

  def readLines(file: File): String =
    using(new BufferedReader(new FileReader(file))) { _.readLine() }

  def justForTravis(home: String): Unit = {
    val configDir = new File(s"$home/.config/")
    configDir.mkdir()
    val hubFile = new File(configDir, "hub")
    using(new BufferedWriter(new FileWriter(hubFile))) { bw =>
      bw.write(
        """- user: travis
          |  oauth_token: 12345678901234567890
          |  protocol: https
          |""".stripMargin
      )
    }
    ()
  }

  lazy val gitHubId: String = try {
    val home = System.getProperty("user.home")
    if (home=="/home/travis") justForTravis(home)
    val hubConfigFile = new File(s"$home/.config/hub")
    if (hubConfigFile.exists)
      readLines(hubConfigFile)
        .split("\n")
        .find(_.contains("user:"))
        .map(_.split(" ").slice(2, 3).mkString)
        .mkString
    else
      "noGithubUserFound"
  } catch {
    case ex: java.io.IOException =>
      System.err.println(s"project/Settings.scala error: ${ ex.getMessage }; ${ ex }")
      sys.exit(-1)
  }
}
