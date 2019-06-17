object PartiallyApplied {
  case class Rider(name: String, weight: Double, height: Double) {
    override def toString = s"""$name is $weight pounds and $height" tall"""
  }
  case class Bike(rider1: Rider, rider2: Rider, rider3: Rider, color: String) {
    lazy val riders = List(rider1, rider2, rider3)
    lazy val names = riders.map(_.name)
    lazy val totalWeight: Double = riders.map(_.weight).sum
    lazy val cheer = s"Go $color team" + names.mkString(": ", ", ", "!")
  }
  val chloe  = Rider("Chloe",  124, 62)
  val louise = Rider("Louise", 136, 68)
  val beth   = Rider("Beth",   112, 59)
  //
  val team1 = Bike(chloe, louise, beth, _: String)
  val bike1 = team1("red")
  val cheer1 = s"${bike1.cheer}"
  //
  val oneRider = Bike(_: Rider, _: Rider, chloe, _: String)
  val team2 = oneRider(louise, beth, _: String)
  val bike2 = team2("blue")
  val riders2 = s"""bike2 riders are: ${bike2.names.mkString(", ")}"""
  //
  val curriedBike = (Bike.apply _).curried
  val curriedBike2 = Bike.curried
  //
  val bike3 = curriedBike(chloe)(louise)(beth)("yellow")
  val weight3 = s"Team ${bike3.color} weighs ${bike3.totalWeight} pounds"
  //
  val oneRiderB1 = Bike.curried(chloe)(_: Rider)(_: Rider)(_: String)
  val oneRiderB2 = Bike.curried(_: Rider)(louise)(_: Rider)(_: String)
  val oneRiderB3 = Bike.curried(_: Rider)(_: Rider)(beth)(_: String)
  val oneRiderC = (Rider.apply _).curried("Beekay")
  val oneRiderD  = Bike.curried(chloe)
  val oneRiderE  = curriedBike(chloe)
  val twoRidersA = Bike.curried(chloe)(louise)
  val twoRidersB = curriedBike(chloe)(louise)
  val threeRidersA = Bike.curried(chloe)(louise)(beth)
  val threeRidersB = curriedBike(chloe)(louise)(beth)
  //
  val team4a = twoRidersA(beth)(_: String)
  val team4b = twoRidersB(beth) // team4a is identical to team4b
  //
  val riderCurried1 = (Rider.apply _).curried
  val riderCurried2 = Rider.curried
  val beekayCurried1 = (Rider.apply _).curried("Beekay")
  val beekayCurried2 = Rider.curried("Beekay")
  //
  val bike4 = team4a("green")
  val weight4 = s"Team ${bike4.color} weighs ${bike4.totalWeight} pounds"
  //
  val uncurriedBike1 = curriedBike _
  val uncurriedBike2 = Function.uncurried(curriedBike)
  val bikex = uncurriedBike2(chloe, louise, beth, "Brown")
}
