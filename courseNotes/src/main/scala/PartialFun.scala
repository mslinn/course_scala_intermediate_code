case class Attendee(name: String, id: Long, knowledge: Option[String])

object PartialFun extends App {
  val attendees = List(
    Attendee("Fred", 1, None),
    Attendee("Lisa", 2, Some("Akka")),
    Attendee("You",  3, Some("Scala"))
  )

  println("Using partial function")
  for {
    you <- attendees.collect { case attendee if attendee.name=="You" => attendee }
    yourKnowledge <- you.knowledge orElse Some("Nothing")
  } yield {
    println(s"You know $yourKnowledge")
  }

  println("\nUsing guard")
  for {
    attendee <- attendees if attendee.name=="You"
    yourKnowledge <- attendee.knowledge orElse Some("Nothing")
  } yield {
    println(s"You know $yourKnowledge")
  }
}
