package com.micronautics.gitStats

import java.nio.file.Paths

import com.micronautics.gitStats.ProjectDir.RichPath
import org.scalatest.FunSuite

class ProjectDirTest extends FunSuite {

  test("fileSuffix - file name is empty string") {
    val res = Paths.get("").fileSuffix
    assert(res === None, "File suffix")
  }

  test("fileSuffix - file name is dot") {
    val res = Paths.get(".").fileSuffix
    assert(res === Some(""), "File suffix")
  }

  test("fileSuffix - file name starts with dot") {
    val res = Paths.get(".gitignore").fileSuffix
    assert(res === Some("gitignore"), "File suffix")
  }

  test("fileSuffix - file name does not have dot") {
    val res = Paths.get("Dockerfile").fileSuffix
    assert(res === None, "File suffix")
  }

  test("fileSuffix - file name has dot") {
    val res = Paths.get("Test.groovy").fileSuffix
    assert(res === Some("groovy"), "File suffix")
  }

  test("fileSuffix - file name has more than one dot") {
    val res = Paths.get("Test.groovy.bkp").fileSuffix
    assert(res === Some("bkp"), "File suffix")
  }

}
