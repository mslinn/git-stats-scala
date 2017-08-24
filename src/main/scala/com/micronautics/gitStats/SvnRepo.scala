package com.micronautics.gitStats

import java.io.File

class SvnRepo(val dir: File)
             (implicit config: ConfigGitStats)  {
  import SvnRepo._
//TODO from and to

//  val author: String = getUserName(getOutputFrom(dir, svnProgram, "auth"))


}

object SvnRepo {
  private val usernamePattern = "Username:\\s+(\\S+)".r

  def getUserNames(svnAuthOutput: String): Set[String] = {
    val allMatches = usernamePattern.findAllIn(svnAuthOutput).matchData
    allMatches.map(_.group(1)).toSet
  }
}
