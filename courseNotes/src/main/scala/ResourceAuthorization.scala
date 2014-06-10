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
  def add(user: User): User = {
    // what goes here?
    user
  }

  /** Add the given resource to the resources HashMap */
  def add(resource: Resource): Resource = {
    // what goes here?
    resource
  }

  /** Use a for-comprehension to write this method
    * @return Some User if the user with the given userId and password is returned by findUserById. */
  def authenticate(userId: String, password: String): Option[User] = ???

  /** Add the given resource to the list of resources assigned to the user in resourceAccess */
  def authorize(user: User, resource: Resource): Unit = ???

  /** @return Some[Resource] if an entry for resourceId is found in resources */
  def findResourceById(resourceId: Long): Option[Resource] = ???

  /** @return Some[User] if an entry for userId is found in users */
  def findUserById(userId: String): Option[User] = ???

  def isUserAuthorized(userId: String, password: String, resourceId: Long): Option[AuthorizationToken] = {
    val token = for {
      user ← authenticate(userId, password)
      resource ← findResourceById(resourceId)
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
  def report(userId: String, password: String, resourceId: Long): Unit = ???

  report("fflintstone", "Yabbadabbadoo",  1)
  report("betty",       "Youtoo",         3)
  report("fflintstone", "ForgotPassword", 1)
  report("nobody",      "nopass",         9)
}
