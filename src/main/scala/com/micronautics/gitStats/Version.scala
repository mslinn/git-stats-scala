package com.micronautics.gitStats

import com.micronautics.gitStats.Version._

/**
  * Multi-component numerical version, like 1.2.3.
  *
  * @param major  Major version component, mandatory.
  * @param minors Minor version components, may be empty.
  */
case class Version(major: Int, minors: Int*) extends Ordered[Version] {

  private val minorsList = minors.toList

  override def compare(that: Version): Int = {
    comparePerComponent(this.major :: this.minorsList,
      that.major :: that.minorsList)
  }

  override def toString: String = (major :: minorsList).mkString(versionSeparator.toString)
}

object Version {

  val versionSeparator: Char = '.'

  def comparePerComponent(versions1: List[Int], versions2: List[Int]): Int = {
    val versionPairs = versions1.zipAll(versions2, 0, 0)
    versionPairs
      .map { case (v1, v2) => v1 compareTo v2 }
      .find(_ != 0)
      .getOrElse(0)
  }

  private val componentPattern = "^(\\d+)\\D*".r

  /**
    * Parse version components from a string like 1.2.3.
    *
    * @param str A string containing version components separated by dots.
    *            Major version component must be a number.
    *            Minor version components may contain non-numerical entries, only numerical prefix will be taken.
    *            For example: '1.2.3-u1' -> 1.2.3
    *            If a component does not start with a number, then set this component to 0.
    *            For example: '1.2.u1' -> 1.2.0
    * @return Parsed version object.
    * @throws IllegalArgumentException Argument is null or empty string.
    * @throws NumberFormatException Major version component is not a number.
    */
  def parse(str: String): Version = {
    require(str != null, "String must not be null")
    require(!str.isEmpty, "String must not be empty")

    val components = str.split(versionSeparator)
    val major = components.head.toInt
    val minors = components.tail.map(c => componentPattern.findFirstMatchIn(c).map(_.group(1).toInt).getOrElse(0))
    Version(major, minors: _*)
  }
}
