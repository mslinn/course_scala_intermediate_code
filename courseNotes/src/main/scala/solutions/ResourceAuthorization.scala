package solutions

object ResourceAuthorization extends App {
  import collection._
  import java.util.Date

  case class User(name: String, password: String, userId: String)
  case class Resource(name: String, id: Long)
  case class AuthorizationToken(user: User, resource: Resource, expires: Date=new Date(System.currentTimeMillis))

  val users          = mutable.HashMap.empty[String, User]
  val resources      = mutable.HashMap.empty[Long, Resource]
  val resourceAccess = mutable.HashMap.empty[User, List[Resource]]

  /** Add the given user to the users HashMap */
  def add(user: User) = {
    users.put(user.userId, user)
    user
  }

  /** Add the given resource to the resources HashMap */
  def add(resource: Resource) = {
    resources.put(resource.id, resource)
    resource
  }

  /** Use a for-comprehension to write this method
    * @return Some User if the user with the given userId and password is returned by findUserById. */
  def authenticate(userId: String, password: String): Option[User] =
    for {
      user <- findUserById(userId) if user.password == password
    } yield user

  /** Add the given resource to the list of resources assigned to the user in resourceAccess */
  def authorize(user: User, resource: Resource): Unit = {
    // preferred over writing resourceAccess.get(user).getOrElse(Nil)
    val oldUserResources: List[Resource] = resourceAccess.getOrElse(user, Nil)
    val updatedUserResources: List[Resource] = resource +: oldUserResources
    resourceAccess.put(user, updatedUserResources)
  }

  /** @return Some[Resource] if an entry for resourceId is found in resources */
  def findResourceById(resourceId: Long): Option[Resource] = resources.get(resourceId)

  /** @return Some[User] if an entry for userId is found in users */
  def findUserById(userId: String): Option[User] = users.get(userId)

  def isUserAuthorized(userId: String, password: String, resourceId: Long): Option[AuthorizationToken] = {
    val token = for {
      user <- authenticate(userId, password)
      resource <- findResourceById(resourceId)
    } yield AuthorizationToken(user, resource)
    token
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
    println(s"$msg2.")
  }

  report("fflintstone", "Yabbadabbadoo",  1)
  report("betty",       "Youtoo",         3)
  report("fflintstone", "ForgotPassword", 1)
  report("nobody",      "nopass",         9)
}
