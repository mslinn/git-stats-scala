package com.micronautics.gitStats.svn

import java.io.File

import com.micronautics.gitStats.Cmd.getOutputFrom
import com.micronautics.gitStats.ConfigGitStats
import com.micronautics.gitStats.svn.SvnCmd.svnProgram

object SvnUsers {

  /**
    * Auto-detect Subversion user names from 'svn auth' command output.
    * @return Set of user names, may be empty if none users found.
    */
  //TODO Provide the ability to explicitly specify Svn user name (in config?) without relying on autodetect
  def autoDetectUserNames(implicit config: ConfigGitStats): Set[String] = {
    val svnAuthCmd = svnProgram :+ "auth"
    val svnAuthOutput = getOutputFrom(new File(sys.props("user.dir")), svnAuthCmd: _*)
    parseUserNames(svnAuthOutput)
  }

  private val usernamePattern = """Username:\s+(\S+)""".r

  /**
    * Parse the output of 'svn auth' command to get Subversion user names.
    * @param svnAuthOutput Output of the command.
    * @return Set of user names, may be empty if none users found.
    */
  def parseUserNames(svnAuthOutput: String): Set[String] = {
    usernamePattern
      .findAllMatchIn(svnAuthOutput)
      .map(_.group(1))
      .toSet
  }
}
