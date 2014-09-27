object EnrichedEnum extends App {
  sealed trait CaseDay
  object CaseDay {
    case object Monday extends CaseDay
    case object Tuesday extends CaseDay
    case object Wednesday extends CaseDay
    case object Thursday extends CaseDay
    case object Friday extends CaseDay
    case object Saturday extends CaseDay
    case object Sunday extends CaseDay
  }

  import CaseDay._
  def tellItLikeItIs(theDay: CaseDay): Unit = {
    val msg = theDay match {
      case Monday => "Mondays are bad."
      case Friday => "Fridays are better."
      case Saturday => "Weekends are best."
      case Sunday => "Weekends are best."
      case _ => "Midweek days are so-so."
    }
    println(msg)
  }
  tellItLikeItIs(Monday)
  tellItLikeItIs(Tuesday)
  tellItLikeItIs(Wednesday)
  tellItLikeItIs(Sunday)
}
