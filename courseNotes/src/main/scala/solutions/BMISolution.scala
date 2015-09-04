package solutions

object BMISolution extends App {
  import solutions.BMICalc._

  case class Person(name: String, gender: Gender, age: Int, height: Meters, weight: Kg)  {
    def formattedStr = f"$name%-25s is $gender%6s, age: $age%3d, height: $height%4.2f Meters, weight: $weight%5.1f Kilos"
    def formattedStrEng = f"$name%-25s is $gender%6s, age: $age%3d, height: ${metersToFtIn(height)._1}%1.0f Ft, ${metersToFtIn(height)._2}%4.1f In, weight: ${kgToLbs(weight)}%6.1f Lbs"
  }
  object Person {
    def makePersonEng(name: String, gender: Gender, age: Int, height: (Ft, In), weight: Lbs): Person =
      Person(name, gender, age, ftInToMeters(height) , lbsToKg(weight))
  }

  val fatAlbert = Person("Fat Albert", Gender.Male, 25, 1.8, 110)
  val oldGuy = Person("Old Guy", Gender.Male, 60, 1.82, 88 )
  val bigBernie = Person("Big Bernie", Gender.Male, 25, 2.6, 400)
  val maxDude = Person("Max Dude",Gender.Male, 20, .5, 560)
  val chandraDangi = Person("Chandra Bahadur Dangi", Gender.Male, 76, .55, 15)
  val khalidShaar = Person("Khalid Bin Mohsen Shaar", Gender.Male, 25, 1.73, 610 )
  val jonMinnoch = Person("Jon Brower Minnoch", Gender.Male, 42, 1.85, 635)
  val sultanKösen = Person("Sultan Kösen", Gender.Male, 35, 2.51, 137)
  val manuelUribe = Person("Manuel Uribe", Gender.Male, 48, 1.96, 597)
  val carolYager = Person("Carol Ann Yager", Gender.Female, 48, 1.7, 544)
  val shortHeavy = Person("Short Heavy",Gender.Male, 20, .5, 560)
  val tallLight = Person("Tall Light", Gender.Female, 60, 2.6, 15)
  val joeFit = Person.makePersonEng("Joe Fit", Gender.Male, 30,(5, 11), 150)
  val atheleticJane = Person.makePersonEng("Athletic Jane", Gender.Female, 35,(5, 5), 105)

  val people = List(fatAlbert, oldGuy, bigBernie, maxDude, chandraDangi, khalidShaar, jonMinnoch, sultanKösen, manuelUribe, carolYager, shortHeavy, tallLight, joeFit, atheleticJane)

  /*
   *  In calculating the BMI we check to see that:
   *    age is 20 or over and 100 or under
   *    weight is between 15 and 640 Kg
   *    height is between .5 meter and 2.6 meters
   */
  val getBMI: PartialFunction[Person, Double] = {
    case person if person.age >= 20 && person.age <= 100 &&
      person.height >= .5 && person.height <= 2.6 &&
      person.weight >= 15 && person.weight <= 640 => bmi(person.weight, person.height)
  }
  val getBMIO = getBMI.lift

  def getBMIDataO(person: Person): Option[(Double, BMICategory, Int)] = {
    if (getBMI.isDefinedAt(person)) {
      val bmi = getBMI(person)
      Option(bmi, BMICategory.getBMICategory(bmi), getBMIPercentile(person.gender, person.age, bmi))
    }
    else None
  }
  val getBMIData: PartialFunction[Person, (Double, BMICategory, Int)] = Function.unlift(getBMIDataO)
  def formatBMIData(bMIData: (Double, BMICategory, Int)): String = f"BMI = ${bMIData._1}%5.1f, Percentile = ${bMIData._3}%2d, ${bMIData._2.formattedStr} "

  println("====People in Metric Units====")
  people.foreach{(person) => {
  print(person.formattedStr + " ")
  println(formatBMIData(getBMIData(person)))
  }}

  println("====People in English Units====")
  people.foreach{(person) => {
    print(person.formattedStrEng + " ")
    println(formatBMIData(getBMIData(person)))
  }}

}
