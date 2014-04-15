object EnvSearch {
  def showEnv(name: String): Seq[String] = {
    import collection.JavaConverters._
    System.getenv.asScala.filter(
      _._2.toLowerCase contains
      name.toLowerCase
    ).values.toList
  }
  showEnv("java").mkString("\n")










}
