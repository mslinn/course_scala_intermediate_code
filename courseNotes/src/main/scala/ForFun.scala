object ForFun extends App {
  val vector = Vector(0, 1, 2, 3)

  println(s"""vector.map(x => x.toString) = ${vector.map(x => x.toString)}""")
  println(s"""List(1, 2, 3).map(_.toString) = ${List(1, 2, 3).map(_.toString)}""")

  for ( i <- 1 to 3 ) println("Hello, world!")

  val array = Array.ofDim[Int](4, 4)
  for {
   i <- 0 until array(0).length
   j <- 0 until array(1).length
  } array(i)(j) = (i+1) * 2*(j+1)
  array.foreach(row => println(row.mkString(", ")))

  for {
    i <- List(1, 2, 3)
    string <- List("a", "b", "c")
  } println(string * i)

  for (i <- 1 to 10 if i % 2 == 0) println(i)
  1 to 10 filter( _ % 2 == 0) foreach { i => println(i) }

  val vector2 = Vector(Some(1), None, Some(3), Some(4))
  vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }
  val fc1 = vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }

  val result = for {
    v <- vector2
    x <- v
  } yield x*2

  val sameResult = for {
    v: Option[Int] <- vector2
    x: Int <- v
  } yield x*2

  val selectedKeys = Map("selectedKeys"->Seq("one", "two", "three"))
  val otherKeys = Map("otherKeys"->Seq("four", "five"))
  val list: List[Map[String, Seq[String]]] = List(selectedKeys, otherKeys)
  val result2: List[String] = for {
    data <- list
    selectedKeysSeq <- data.get("selectedKeys").toList
    id <- selectedKeysSeq.toList
  } yield id

  val result3: List[String] = for {
    data: Map[String, Seq[String]] <- list
    selectedKeysSeq: Seq[String] <- data.get("selectedKeys").toList
    id: String <- selectedKeysSeq.toList
  } yield id

  val result4: List[String] = list.flatMap { data: Map[String, Seq[String]] =>
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq: Seq[String] =>
      selectedKeysSeq
    }
  }

  println(s"""result = $result""")
  println(s"""result2 = $result2""")
  println(s"""result3 = $result3""")
  println(s"""result4 = $result4""")
}

object ResourceAuthorization extends App {
  import collection._
  import java.util.Date

  case class User(name: String, password: String, userId: String)
  case class Resource(name: String, id: Long)
  case class AuthorizationToken(user: User, resource: Resource, expires: Date=new Date(System.currentTimeMillis))

  val users = mutable.HashMap.empty[String, User]
  val resources = mutable.HashMap.empty[Long, Resource]
  val resourceAccess = mutable.HashMap.empty[User, List[Resource]]

  def add(user: User) = {
    users.put(user.userId, user)
    user
  }

  def add(resource: Resource) = {
    resources.put(resource.id, resource)
    resource
  }

  def authenticate(userId: String, password: String): Option[User] =
    for {
      user <- findUserById(userId) if user.password == password
    } yield user

  def authorize(user: User, resource: Resource): Unit = {
    val newResources: List[Resource] = resource +: resourceAccess.get(user).getOrElse(Nil)
    resourceAccess.put(user, newResources)
  }

  def findResourceById(resourceId: Long): Option[Resource] = resources.get(resourceId)

  def findUserById(userId: String): Option[User] = users.get(userId)

  def isUserAuthorized(user: User, resource: Resource): Boolean = resourceAccess.get(user).isDefined

  /** @return Option[authorizationToken] */
  def isUserAuthorized(userId: String, password: String, resourceId: Long): Option[AuthorizationToken] = {
    val result = for {
      user <- authenticate(userId, password)
      resource <- findResourceById(resourceId)
    } yield AuthorizationToken(user, resource)
    result
  }

  val fred   = add(User("Fred Flintstone", "Yabbadabbadoo", "fflintstone"))
  val wilma  = add(User("Wilma Flintstone", "Friendship", "wflintstone"))
  val barney = add(User("Barney Rubble", "Huyuk", "brubble"))
  val betty  = add(User("Betty Rubble", "Youtoo", "betty"))

  val work           = add(Resource("Work Cave", 1))
  val flintstoneHome = add(Resource("Flintstone home", 2))
  val rubbleHome     = add(Resource("Rubble home", 3))

  authorize(fred,   flintstoneHome)
  authorize(wilma,  flintstoneHome)
  authorize(fred,   work)
  authorize(barney, rubbleHome)
  authorize(betty,  rubbleHome)
  authorize(barney, work)

  /** For-comprehensions would not be as convenient here */
  def report(userId: String, password: String, resourceId: Long): Unit = {
    val maybeAuthToken: Option[AuthorizationToken] = isUserAuthorized(userId, password, resourceId)
    val resourceName = findResourceById(resourceId).map(_.name).getOrElse(s"resource with Id $resourceId")
    val userName = findUserById(userId).map(_.name).getOrElse(s"User with id $userId")
    val msg = s"$userName and password $password ${ if (maybeAuthToken.isDefined) "can" else "can not" } access resource $resourceName"
    val msg2 = maybeAuthToken.map { msg + " until " + _.expires.toString }.getOrElse(msg)
    println(msg2)
  }

  report("fflintstone", "Yabbadabbadoo",  1)
  report("betty",       "Youtoo",         3)
  report("fflintstone", "ForgotPassword", 1)
  report("nobody",      "nopass",         1)
}
