import com.typesafe.config.{ConfigObject, ConfigList, ConfigFactory}
import java.util.concurrent.TimeUnit._

object ConfigFun extends App {
  import com.typesafe.config.{ConfigRenderOptions, Config}

  val confDemo: Config = ConfigFactory.parseResources("demo.properties")
  val string1 = confDemo.getString("string1")
  val int1 = confDemo.getInt("int1")
  val double1 = confDemo.getDouble("double1")
  val elapsedDays = confDemo.getDuration("elapsedTime", DAYS)
  val elapsedHours = confDemo.getDuration("elapsedTime", HOURS)
  val elapsedSeconds = confDemo.getDuration("elapsedTime", SECONDS)
  val bytes1 = confDemo.getBytes("bytes1")
  val bytes2 = confDemo.getBytes("bytes2")
  println(s"""string1=$string1""")
  println(s"""int1=$int1""")
  println(s"""double1=$double1""")
  println(s"""elapsedDays=$elapsedDays""")
  println(s"""elapsedHours=$elapsedHours""")
  println(s"""elapsedSeconds=$elapsedSeconds""")
  println(s"""bytes1=$bytes1""")
  println(s"""bytes2=$bytes2""")

  val options = ConfigRenderOptions.concise.setJson(true).setFormatted(true)
  val json = confDemo.root.render(options)
  println(s"""demo.properties converted to JSON: $json""")

  println()

  val confDemo2 = ConfigFactory.parseResources("demo2.properties").withFallback(confDemo)
  val string1b = confDemo2.getString("string1")
  val int1b = confDemo2.getInt("int1")
  val double1b = confDemo2.getDouble("double1")
  val double2 = confDemo2.getDouble("double2")
  println(s"""string1b=$string1b""")
  println(s"""int1b=$int1b""")
  println(s"""double1b=$double1b""")
  println(s"""double2=$double2""")

  val confOverride = ConfigFactory.parseResources("override.conf").resolve
  val blarg = confOverride.getString("blarg")
  println(s"""blarg=$blarg""")

  val confDemo3 = ConfigFactory.parseResources("demo.json").withFallback(confDemo2)
  val string1c = confDemo3.getString("string1")
  val int1c = confDemo3.getInt("int1")
  val double1c = confDemo3.getDouble("double1")
  val double2b = confDemo3.getDouble("double2")
  val firstName = confDemo3.getString("firstName")
  val isAlive = confDemo3.getString("isAlive")
  val height_cm = confDemo3.getString("height_cm")
  val phoneNumbers: ConfigList = confDemo3.getList("phoneNumbers")
  val phoneType = phoneNumbers.atKey("type")
  val phoneNumber = phoneNumbers.atKey("number")
  val streetAddress = confDemo3.getString("address.streetAddress")
  val city = confDemo3.getString("address.city")
  val addresses: ConfigObject = confDemo3.getObject("address")
  val streetAddress2 = addresses.toConfig.getString("streetAddress")
  val city2 = addresses.toConfig.getString("city")
  println(s"""string1c=$string1c""")
  println(s"""int1c=$int1c""")
  println(s"""double1c=$double1c""")
  println(s"""double2b=$double2b""")
  println(s"""firstName=$firstName""")
  println(s"""isAlive=$isAlive""")
  println(s"""height_cm=$height_cm""")
  println(s"""phoneType=$phoneType""")
  println(s"""phoneNumber=$phoneNumber""")
  println(s"""streetAddress=$streetAddress""")

  val conf = ConfigFactory.load
  val value1 = conf.getString("keyName1")
  val value2 = conf.getBoolean("keyName2")
  val value3 = conf.getInt("nested.key.name3")
  val confNested = conf.getConfig("nested.key.name4")
  val value5 = confNested.getInt("one")
  val value6 = confNested.getInt("two")
  println(s"""value1=$value1""")
  println(s"""value2=$value2""")
  println(s"""value3=$value3""")
  println(s"""value3=$value3""")
  println(s"""confNested=$confNested""")
  println(s"""value5=$value5""")
  println(s"""value6=$value6""")

  val string = """
    |keyName1 : "Value 1"
    |keyName2 = true
    |nested.key.name3 : 5
    |nested { key { name4 = { "one" : 10, "two" : 12 } } }
    |array : [ 1, 2, 3]
    |""".stripMargin
  val conf2 = ConfigFactory.parseString(string)
  val value1b = conf2.getString("keyName1")
  val value2b = conf2.getBoolean("keyName2")
  val value3b = conf2.getInt("nested.key.name3")
  val confNested2 = conf2.getConfig("nested.key.name4")
  val value5b = confNested2.getInt("one")
  val value6b = confNested2.getInt("two")
  println(s"""conf2=$conf2""")
  println(s"""value1b=$value1b""")
  println(s"""value2b=$value2b""")
  println(s"""value3b=$value3b""")
  println(s"""confNested2=$confNested2""")
  println(s"""value5b=$value5b""")
  println(s"""value6b=$value6b""")

  import scala.jdk.CollectionConverters._
  val keys = conf2.entrySet.asScala.map(_.getKey)
  val keyName1 = conf2.getString("keyName1")
  val array = conf2.getIntList("array")
  val confNested3 = conf2.getConfig("nested")
  val keys2 = confNested3.entrySet.asScala.map(_.getKey)
  val keyName3 = confNested3.getString("key.name3")
  println(s"""keys=$keys""")
  println(s"""keyName1=$keyName1""")
  println(s"""array=$array""")
  println(s"""confNested3=$confNested3""")
  println(s"""keys2=$keys2""")
  println(s"""keyName3=$keyName3""")
}
