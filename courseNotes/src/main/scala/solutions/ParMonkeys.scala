package solutions

object ParMonkeys extends App {
  val target = "I thought I saw a lolcat! I did, I did see a lolcat!"

  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

  val random = util.Random

  val allowableChars = """ !.,;'""" + (('a' to 'z').toList ::: ('A' to 'Z').toList
      ::: (0 to 9).toList).mkString

  def randomString(n: Int) = (1 to n).map { _ =>
    val i = random.nextInt(allowableChars.length-1)
    allowableChars.substring(i, i+1)
  }.mkString

  /** return longer of s1 and s2 */
  def longestStr (s1:String, s2: String) = if (s1.length>= s2.length) s1 else s2

  /** find the longest common substring where the target is matched against each segment of monkeyString */
  def simMonkeys (numSims: Int, simStrLen: Int, target: String): String = {
    val matchLimit = simStrLen - target.length

    /** find the longest common substring where the target is matched against each segment of monkeyString */
    def longestCommonSubstring(monkeyString: String): String = {
      (0 until matchLimit).par
        .map(j => matchSubstring(monkeyString.drop(j), target))
        .foldLeft("")(longestStr)
    }

    (1 to numSims).par
      .map(_ => longestCommonSubstring(randomString(simStrLen)))
      .foldLeft("")(longestStr)
  }
  println("Longest common substring: '" + simMonkeys(50000, 100, target) + "'")
}
