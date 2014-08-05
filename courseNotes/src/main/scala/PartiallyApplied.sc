object PartiallyApplied {
  case class Rider(name: String, weight: Double, height: Double) {
    override def toString = s"$name weighs $weight pounds and is $height inches tall"
  }

  case class Bike(rider1: Rider, rider2: Rider, rider3: Rider, color: String) {
    lazy val riders = List(rider1, rider2, rider3)
    lazy val names = riders.map(_.name)
    lazy val totalWeight: Double = riders.map(_.weight).sum
    lazy val cheer = s"Go $color team" + names.mkString(": ", ", ", "!")
  }

  val chloe  = new Rider("Chloe",  124, 62)
  val louise = new Rider("Louise", 136, 68)
  val beth   = new Rider("Beth",   112, 59)

  val team1 = Bike(chloe, louise, beth, _: String)
  val bike1 = team1("red")
  val cheer1 = s"${bike1.cheer}"

  val oneRider = Bike(_: Rider, _: Rider, chloe, _: String)
  val team2 = oneRider(louise, beth, _: String)
  val bike2 = team2("blue")
  val riders2 = s"""bike2 riders are: ${bike2.names.mkString(", ")}"""

  val curriedBike = Bike.curried
  val bike3 = curriedBike(chloe)(louise)(beth)("yellow")
  val weight3 = s"Team ${bike3.color} weighs ${bike3.totalWeight} pounds"

  val oneRiderB   = Bike.curried(chloe)(_: Rider)(_: Rider)(_: String)
  val oneRiderC   = Bike.curried(chloe) _
  val oneRiderD   = curriedBike(chloe) _

  val twoRidersA  = Bike.curried(chloe)(louise) _
  val twoRidersB  = curriedBike(chloe)(louise) _

  val threeRidersA = Bike.curried(chloe)(louise)(beth) _
  val threeRidersB = curriedBike(chloe)(louise)(beth) _

  val team4a = twoRidersA(beth)(_: String)
  val team4b = twoRidersB(beth) _ // team3a is identical to team3b

  val bike4 = team4a("green")
  val weight4 = s"Team ${bike4.color} weighs ${bike4.totalWeight} pounds"
}
