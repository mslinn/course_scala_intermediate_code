package solutions

object BMICalc {

  /*
   * We define types for the Metric and English units for documentation purposes, and to keep our units straight
   */
  type Kg = Double
  type Meters = Double

  /*
   * These function both calculate BMI, given metric units. bmiPartial is a partial function that does some input checking.
   */
  def bmi(weight: Kg, height: Meters): Double = weight / (height * height)
  val bmiPartial: PartialFunction[(Kg, Meters), Double] = {
    case (weight, height) if weight >= 15 && weight <= 640 && height >= .5 && height <= 2.6 => weight / (height * height)
  }

  /*
   * The following  calculates the set of BMI categories, taken from https://en.wikipedia.org/wiki/Body_mass_index
   * There is some feeling that these categories are actually unrealistic.
   */
  sealed trait BMICategory {
    def msg: String
    def bMIRange: (Double, Double)
    def inRange(bmi: Double): Boolean = { bmi >= bMIRange._1 && bmi <= bMIRange._2  }
    def formattedStr = f"BMI category is $msg%20s (BMI range: ${bMIRange._1}%5.2f,${bMIRange._2}%5.2f)"
  }
  object BMICategory {
    case object VeryServerlyUnderWeight extends BMICategory {val msg = "Very severely underweight";val bMIRange = (0.0, 15.0)}
    case object ServerlyUnderWeight extends BMICategory {val msg = "Severely underweight";val bMIRange = (15.0, 16.0)}
    case object UnderWeight extends BMICategory {val msg = "Underweight";val bMIRange = (16.0, 18.5)}
    case object NormalWeight extends BMICategory {val msg = "Normal weight";val bMIRange = (18.5, 25.0)}
    case object OverWeight extends BMICategory {val msg = "Overweight";val bMIRange = (25.0, 30.0)}
    case object ModeratelyObese extends BMICategory {val msg = "Moderately Obese";val bMIRange = (30.0, 35.0)}
    case object SeverelyObese extends BMICategory {val msg = "Severely Obese";val bMIRange = (35.0, 40.0)}
    case object VerySeverelyObese extends BMICategory {val msg = "Very Severely Obese";val bMIRange = (40.0, 5000.0)}
    val bmiRanges = List(VeryServerlyUnderWeight, ServerlyUnderWeight, UnderWeight, NormalWeight, OverWeight, ModeratelyObese, SeverelyObese, VerySeverelyObese)
    val getBMICategory  = Function.unlift((x: Double) => bmiRanges.find(_.inRange(x)))
  }

  /*
   *  This is a simple gender trait to tag people as Male or Female
   */
  sealed trait Gender {
    def msg: String
    def isMale: Boolean
    def isFemale: Boolean
    override def toString = msg
  }
  object Gender {
    case object Male extends Gender {
      val msg = "Male"
      val isMale = true
      val isFemale = false
    }
    case object Female extends Gender {
      val msg = "Female"
      val isMale = false
      val isFemale = true
    }
  }

  /*
   * The following code calculates where a particular BMI sits against the North American population, as a percentile.
   * The data is taken from https://en.wikipedia.org/wiki/Body_mass_index
   * Note we have added a "99th" percentile with a very high BMI value (5000) to avoid lookups of ridiculously high BMI
   * values from failing.
   */
  val percentiles = List(5, 10, 15, 25, 50, 75, 85, 90, 95, 99 )
  val ages = List(29, 39, 49, 59, 69,79, 120)
  //Data for males
  val age29m = List(19.4,	20.7,	21.4,	22.9,	25.6,	29.9,	32.3,	33.8,	36.5, 5000) zip percentiles
  val age39m = List(21.0,	22.4,	23.3,	24.9,	28.1,	32.0,	34.1,	36.2,	40.5, 5000) zip percentiles
  val age49m = List(21.2,	22.9,	24.0,	25.4,	28.2,	31.7,	34.4,	36.1,	39.6, 5000) zip percentiles
  val age59m = List(21.5,	22.9,	23.9,	25.5,	28.2,	32.0,	34.5,	37.1,	39.9, 5000) zip percentiles
  val age69m = List(21.3,	22.7,	23.8,	25.3,	28.8,	32.5,	34.7,	37.0,	40.0, 5000) zip percentiles
  val age79m = List(21.4,	22.9,	23.8,	25.6,	28.3,	31.3,	33.5,	35.4,	37.8, 5000) zip percentiles
  val age99m = List(20.7,	21.8,	22.8,	24.4,	27.0,	29.6,	31.3,	32.7,	34.5, 5000) zip percentiles
  val maleTable = ages zip List(age29m, age39m, age49m, age59m, age69m, age79m, age99m )
  //Data for females
  val age29f = List(18.8,	19.9,	20.6,	21.7,	25.3,	31.5,	36.0,	38.0,	43.9, 5000) zip percentiles
  val age39f = List(19.4,	20.6,	21.6,	23.4,	27.2,	32.8,	36.0,	38.1,	41.6, 5000) zip percentiles
  val age49f = List(19.3,	20.6,	21.7,	23.3,	27.3,	32.4,	36.2,	38.1,	43.0, 5000) zip percentiles
  val age59f = List(19.7,	21.3,	22.1,	24.0,	28.3,	33.5,	36.4,	39.3,	41.8, 5000) zip percentiles
  val age69f = List(20.7,	21.6,	23.0,	24.8,	28.8,	33.5,	36.6,	38.5,	41.1, 5000) zip percentiles
  val age79f = List(20.1,	21.6,	22.7,	24.7,	28.6,	33.4,	36.3,	38.7,	42.1, 5000) zip percentiles
  val age99f = List(19.3,	20.7,	22.0,	23.1,	26.3,	29.7,	31.6,	32.5,	35.2, 5000) zip percentiles
  val femaleTable = ages zip List(age29f, age39f, age49f, age59f, age69f, age79f, age99f )
  /*
   * We use a find, by age, on maleTable or femaleTable, extract the list of percentiles for a given age,
   * then use a find, by BMI, and extract the percentile
   * A naive implementation using combinators is:
   *    maleTable.find(age <= _._1).get._2.find(bmi <= _._1).get._2
   * The problem is that find returns an Option... and we are not handling the cases where a None is returned by the finds.
   */
  def getBMIPercentileBad(gender: Gender, age: Int, bmi: Double): Int =
    maleTable.find(age <= _._1).get._2.find(bmi <= _._1).get._2   //Find returns an option

  /*
   *  This for comprehension essentially does the same thing as getBMIPercentileBad,
   *  but it will properly handle the case where a None is returned
   */
  def getBMIPercentileO(gender: Gender, age: Int, bmi: Double) = {
    val theTable = if (gender.isMale) maleTable else  femaleTable
    for  {
      theTableO <- theTable.find(age <= _._1)
      bmiLookUpTable <- Option(theTableO._2)
      percentile <- bmiLookUpTable.find(bmi <= _._1)
    } yield percentile._2
  }
  /*
   * Create a Partial Function version of  getBMIPercentileO. The call to tupled converts the individual function
   * parameters into a single tuple, because a PartialFunction only takes a single parameter.
   */
  val getBMIPercentile: PartialFunction[(Gender, Int, Double), Int] = Function.unlift (Function.tupled(getBMIPercentileO))


  /*
   * Conversion functions from English to Metric units, so English Units can be converted to be used with the
   * bmi function
   */
  type Lbs = Double
  type Ft = Double
  type In = Double
  val LbsToKg = 2.2046
  def lbsToKg(lbs: Lbs): Kg = lbs / LbsToKg
  val lbsToKgPartial: PartialFunction[Lbs, Kg] = {
    case lbs if lbs > 0 => lbsToKg(lbs)
  }
  def kgToLbs(kg: Kg): Lbs = kg * LbsToKg

  val MetersToInches = 39.370
  val MetersToFt =  3.2808
  def ftInToMeters(height: (Ft, In)): Meters = (height._1 / MetersToFt) + (height._2 / MetersToInches)
  val ftInToMetersPartial: PartialFunction[(Ft, In), Meters] = {
    case (ft, in) if ft > 0 && in >= 0 && in < 12 => ftInToMeters(ft, in)
  }
  def metersToFtIn(meters: Meters): (Ft, In) = {
    val grossIn = meters *  MetersToInches
    val ft = (grossIn / 12).floor
    (ft, grossIn - (ft * 12))
  }

}
