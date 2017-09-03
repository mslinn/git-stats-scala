package com.micronautics.gitStats

package object svn {

  lazy val svnProgram: String = if (Cmd.isWindows) "svn.exe" else "svn"
}
