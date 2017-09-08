package com.micronautics.gitStats.svn

import com.micronautics.gitStats.ConfigGitStats
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnCmd._
import com.micronautics.gitStats.svn.SvnWorkDir._

//TODO Embed into the main app, remove main method from here
object SvnStats extends App {

  implicit val config: ConfigGitStats = ConfigGitStats()
  if (svnVersion.isEmpty) {
    Console.err.println("Failed to determine Subversion command version; exiting")
  }
  Console.err.println(s"Subversion command version: $svnVersion")

  if (svnVersion.get < svnMinimalSupportedVersion) {
    Console.err.println(s"Unsupported Subversion version: $svnVersion; $svnMinimalSupportedVersion or higher is required")
    sys.exit(-1)
  }

  val svnUsers: SvnUsers = new SvnUsers()
  println(s"Subversion user names: ${svnUsers.userNames}")
  if (svnUsers.userNames.isEmpty) {
    Console.err.println("Failed to detect Subversion users; exiting")
    sys.exit(-1)
  }

  val svnCmd = new SvnCmd()
  lazy val svnLogCmd: Iterable[String] = List(svnProgram, "log", "--diff") ++
    svnUsers.userNames.map(userName => s"--search $userName") ++
    List(svnCmd.dateRangeOption)


  lazy val workDirs = findProjectDirs(config.directory.toPath)(isSvnWorkDir)
}
