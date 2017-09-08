package com.micronautics.gitStats.svn

import com.micronautics.gitStats.{ConfigGitStats, Version}

//TODO Embed into the main app, remove main method from here
object SvnStats extends App {

  implicit val config: ConfigGitStats = ConfigGitStats()
  val svnCmd: SvnCmd = new SvnCmd()
  if (svnCmd.svnVersion.isEmpty) {
    Console.err.println("Failed to determine Subversion command version; exiting")
  }
  val svnVersion: Version = svnCmd.svnVersion.get
  Console.err.println(s"Subversion command version: $svnVersion")

  /*
  * --search is available from 1.8 onward.
  * TODO Move to SvnCmd
  * */
  val svnMinimalSupportedVersion = Version.parse("1.8")
  if (svnVersion < svnMinimalSupportedVersion) {
    Console.err.println(s"Unsupported Subversion version: $svnVersion; $svnMinimalSupportedVersion or higher is required")
    sys.exit(-1)
  }

  val svnUsers: SvnUsers = new SvnUsers()
  println(s"Subversion user names: ${svnUsers.userNames}")
  if (svnUsers.userNames.isEmpty) {
    Console.err.println("Failed to detect Subversion users; exiting")
    sys.exit(-1)
  }
}
