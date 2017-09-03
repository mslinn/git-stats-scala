package com.micronautics.gitStats.svn

import java.io.File

import com.micronautics.gitStats.svn.SvnUsers._
import com.micronautics.gitStats.{Cmd, ConfigGitStats}

class SvnUsers(implicit config: ConfigGitStats) {
  //TODO Provide the ability to explicitly specify Svn user name (in config?) without relying on autodetect
  lazy val userNames: Set[String] = autoDetectUserNames

  /**
    * Auto-detect Subversion user names from 'svn auth' command output.
    * @return Set of user names, may be empty if none users found.
    */
  protected def autoDetectUserNames: Set[String] = {
    val svnAuthOutput = Cmd.getOutputFrom(new File(sys.props("user.dir")), svnProgram, "auth")
    parseUserNames(svnAuthOutput)
  }
}

object SvnUsers {

  private val usernamePattern = "Username:\\s+(\\S+)".r

  /**
    * Parse the output of 'svn auth' command to get Subversion user names.
    * @param svnAuthOutput Output of the command.
    * @return Set of user names, may be empty if none users found.
    */
  def parseUserNames(svnAuthOutput: String): Set[String] = {
    val allMatches = usernamePattern.findAllIn(svnAuthOutput).matchData
    allMatches.map(_.group(1)).toSet
  }
}
