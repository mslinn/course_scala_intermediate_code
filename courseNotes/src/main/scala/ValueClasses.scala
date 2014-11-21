
object WeaklyTypedParameters extends App {
  import collection.mutable.ListBuffer
  import Permission._

  case class User(name: String, permissions: Seq[Permission], id: Option[Int]=None)

  object Users {
    private val users = ListBuffer.empty[User]

    def add(user: User) = {
      users += user
      this
    }

    def findByName(name: String): Option[User] =
      users.find(_.name==name).headOption
  }

  implicit class RichUser(user: User) {
    def authorize(granteeName: String, permission: Permission): Option[User] =
      for {
        grantor <- Some(user) if user.permissions.contains(PARENT)
        grantee <- Users.findByName(granteeName).headOption
      } yield grantee.copy(permissions = grantee.permissions :+ permission)
  }

  val wilma = User("Wilma Flintstone", List(PARENT), Some(1))
  val pebbles = User("Pebbles Flintstone", Nil, Some(2))
  Users.add(wilma).add(pebbles)

  wilma.authorize(pebbles.name, CHILD) match {
    case Some(child) =>
      println(s"${child.name} got upgraded permissions")

    case None =>
      println("Authorization failed")
  }

  pebbles.authorize(wilma.name, CHILD) match {
    case Some(child) =>
      println(s"${child.name} got upgraded permissions")

    case None =>
      println("Authorization failed")
  }
}

object StronglyTypedParameters extends App {
  import collection.mutable.ListBuffer
  import Permission._

  case class User(name: String, permissions: Seq[Permission], id: Option[Int]=None) {
    def asGrantee: Grantee = Grantee(this)

    def asGrantor: Grantor = Grantor(this)
  }

  case class Grantor(user: User) extends AnyVal {
    def authorize(grantee: Grantee, permission: Permission): Option[Grantee] = {
      if (user.permissions.contains(PARENT))
        Some(grantee.withPermission(permission))
      else
        None
    }
  }

  case class Grantee(user: User) extends AnyVal {
    def withPermission(permission: Permission): Grantee = {
      val newUser = User(user.name, user.permissions :+ permission, user.id)
      Grantee(newUser)
    }
  }

  object Users {
    private val users = ListBuffer.empty[User]

    def add(user: User) = {
      users += user
      this
    }

    def findByName(name: String): Option[User] =
      users.find(_.name==name).headOption
  }

  val wilma = User("Wilma Flintstone", List(PARENT), Some(1))
  val pebbles = User("Pebbles Flintstone", Nil, Some(2))
  Users.add(wilma).add(pebbles)

  wilma.asGrantor.authorize(pebbles.asGrantee, CHILD) match {
    case Some(Grantee(child)) =>
      println(s"${child.name} now has permissions ${child.permissions.mkString(", ")}")

    case None =>
      println("Authorization failed")
  }

  /* fails typecheck:
  pebbles.asGrantee.authorize(wilma.asGrantor, CHILD) match {
    case Some(child) =>
      println((s"${child.name} now has permissions ${child.permissions.mkString(", ")}")

    case None =>
      println("Authorization failed")
  }
  */
}
