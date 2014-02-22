package solutions

import java.io.{File,PrintWriter}

object FileReverser extends App {
  val inputFileName  = Console.readLine("Input file name: ")
  val outputFileName = Console.readLine("Output file name: ")

  val contents = io.Source.fromFile(inputFileName).mkString
  val reversedWords = contents.split(" ").map(_.reverse).mkString(" ")

  val writer = new PrintWriter(new File(outputFileName))
  writer.write(reversedWords)
  writer.close()
}
