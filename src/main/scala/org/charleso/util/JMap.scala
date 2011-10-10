package org.charleso.util

import java.io.File
import org.apache.commons.io.comparator.NameFileComparator
import scala.io.Source
import org.apache.commons.io.FileUtils

import scalaz._
import Scalaz._

object JMap {

  type Line = List[(Int, Int)]

  def main(args: Array[String]) = println(filterAll(parseAll(args(0)), instances).take(100).mkString("\n"))

  def parseAll(dir: String) = new File(dir).listFiles().sorted
    .par
    .map(f => parseLines(Source.fromFile(f).getLines()))
    .reduce(_ |+| _)

  def parseLines(lines: Iterator[String]) = {
    lines.dropWhile(!_.contains(":"))
      .filter(!_.startsWith("Total"))
      .map(parseLine(_))
      .toTraversable
      .groupBy(_._1)
      .mapValues(l => (l.map(_._2).sum, l.map(_._3).sum))
      .mapValues(List(_))
  }

  def filterAll(map: Map[String, Line], filter: ((Int, Int)) => Int) = map
    .filter(_._2.distinct.size != 1)
    .mapValues(_.map(filter))
    .filter(a => a._2.sorted equals a._2)
    .mapValues(a => (a.max - a.min) -> a).toList.sortBy(_._2._1).reverse

  val instances = (i: (Int, Int)) => i._1

  val bytes = (i: (Int, Int)) => i._2

  def parseLine(line: String) = {
    val a = line.replaceAll(" +", " ").trim.split(" ")
    (fixName(a(3)), a(1).toInt, a(2).toInt)
  }

  def fixName(name: String) = {
    if (name.startsWith("sun.reflect.GeneratedMethodAccessor")) {
      "sun.reflect.GeneratedMethodAccessor"
    } else if (name.startsWith("$Proxy")) {
      "$Proxy"
    } else {
      name
    }
  }
}