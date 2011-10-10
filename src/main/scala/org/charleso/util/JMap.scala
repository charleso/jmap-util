package org.charleso.util

import java.io.File
import org.apache.commons.io.comparator.NameFileComparator
import scala.io.Source
import org.apache.commons.io.FileUtils

import scalaz._
import Scalaz._

object JMap {

  def main(args: Array[String]) = {
    val maxLines = 100;
    val instances = (i: (Int, Int)) => i._1
    val bytes = (i: (Int, Int)) => i._2
    val filter = if (args.contains("--instances")) instances else bytes

    val files = new File(args(0)).listFiles();

    def parse(f: File) = {
      Source.fromFile(f).getLines()
        .dropWhile(!_.contains(":"))
        .toTraversable
        .filter(!_.startsWith("Total"))
        .map(parseLine(_))
        .groupBy(_._1)
        .mapValues(l => (l.map(_._2).sum, l.map(_._3).sum))
        .mapValues(List(_))
    }
    val stuff = files.sorted.map(parse(_)).reduce(_ |+| _)
      .filter(_._2.distinct.size != 1)
      .mapValues(_.map(filter))
      // What grows
      .filter(a => a._2.sorted equals a._2)
      .mapValues(a => (a.max - a.min) -> a).toList.sortBy(_._2._1).reverse
      .take(maxLines)

    System.out.println(stuff.mkString("\n"))
  }

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