package com.micronautics.gitStats

import java.nio.file.Paths

import org.scalatest.FunSuite

class FileModificationTest extends FunSuite {

  implicit val config: ConfigGitStats = ConfigGitStats()

  test("FileModification - file is null") {
    intercept[IllegalArgumentException] {
      FileModification(null, 4, 5)
    }
  }

  test("FileModification - file path is empty string") {
    intercept[IllegalArgumentException] {
      FileModification(Paths.get(""), 4, 5)
    }
  }

}
