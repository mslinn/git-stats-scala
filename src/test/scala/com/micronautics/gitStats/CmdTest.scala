package com.micronautics.gitStats

import java.nio.file.Paths

import org.scalatest.FunSuite

class CmdTest extends FunSuite {

  implicit val config: ConfigGitStats = ConfigGitStats(verbose = true)

  test("getOutputFrom - command does not exist") {
    try {
      Cmd.getOutputFrom(Paths.get(sys.props("user.home")), "abyrvalg")
      fail("Must throw an exception")
    } catch {
      case e: Exception =>
        assert(e.getMessage.contains("abyrvalg"), "Must contain command")
        assert(e.getMessage.contains("not found"), "Must complain about not found command")
    }
  }

  test("getOutputFrom - nonzero return code") {
    val prog = if (Cmd.isWindows) "cmd /c exit 1" else "/bin/false"
    try {
      Cmd.getOutputFrom(Paths.get(sys.props("user.home")), prog)
      fail("Must throw an exception")
    } catch {
      case e: Exception =>
        assert(e.getMessage.contains("Nonzero"), "Must complain about nonzero exit code")
    }
  }
}
