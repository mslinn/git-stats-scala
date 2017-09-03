package com.micronautics.gitStats.svn

import com.micronautics.gitStats.ConfigGitStats

//TODO Embed into th main app, remove main method from here
object SvnStats extends App {

  implicit val config: ConfigGitStats = ConfigGitStats()
  val svnCmd: SvnCmd = new SvnCmd()
  println(s"Subversion command version: ${svnCmd.svnVersion}")

  val svnUsers: SvnUsers = new SvnUsers()
  println(s"Subversion user names: ${svnUsers.userNames}")
}
