package com.micronautics.gitStats

import com.micronautics.gitStats.Version._

import scala.math.signum

/**
  * Multi-component version, like 1.2.3
  * @param major Major version component, mandatory and non-empty.
  * @param minors Minor version components, may be empty.
  */
case class Version(major: String, minors: String*) extends Ordered[Version] {
  require(major != null, "Major version must not be null")
  require(!major.isEmpty, "Major version must not be empty string")

  private val minorsList = minors.toList

  override def compare(that: Version): Int = {
    comparePerComponent(this.major :: this.minorsList,
      that.major :: that.minorsList)
  }

  override def toString: String = (major :: minorsList).mkString(versionSeparator.toString)
}

object Version {

  val versionSeparator: Char = '.'

  def comparePerComponent(versions1: List[String], versions2: List[String]): Int = {
    val versionPairs = versions1.zipAll(versions2, "0", "0")
    versionPairs
      .map { case (v1, v2) => signum(v1 compareTo v2) }
      .find(_ != 0)
      .getOrElse(0)
  }

  def parse(str: String): Version = {
    require(str != null, "String must not be null")
    require(!str.isEmpty, "String must not be empty")

    val components = str.split(versionSeparator)
    Version(components.head, components.tail: _*)
  }
}
