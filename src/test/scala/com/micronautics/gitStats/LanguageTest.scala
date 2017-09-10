package com.micronautics.gitStats

import java.nio.file.Paths

import com.micronautics.gitStats.Language._
import org.scalatest.FunSuite

class LanguageTest extends FunSuite {

  test("fileLanguage - file is null") {
    intercept[IllegalArgumentException] {
      fileLanguage(null)
    }
  }

  test("fileLanguage - file name is empty string") {
    intercept[IllegalArgumentException] {
      fileLanguage(Paths.get(""))
    }
  }

  test("fileLanguage - file name has known suffix") {
    val res = fileLanguage(Paths.get("Test.scala"))
    assert(res === "Scala", "File language")
  }

  test("fileLanguage - file name does not have suffix but is known name") {
    val res = fileLanguage(Paths.get("Dockerfile"))
    assert(res === "Dockerfile", "File language")
  }

  test("fileLanguage - file name starts with dot") {
    val res = fileLanguage(Paths.get(".gitignore"))
    assert(res === "Miscellaneous", "File language")
  }

  test("fileLanguage - file does not have suffix but its content is Shell") {
    val path = Paths.get(sys.props("user.dir"), "bin", "run")
    if (path.toFile.canRead) {
      val res = fileLanguage(path)
      assert(res === "Shell", "File language")
    }
  }

  test("fileLanguage - file does not exist") {
    val res = fileLanguage(Paths.get("/no/such/file"))
    assert(res === "Unknown", "File language")
  }

}
