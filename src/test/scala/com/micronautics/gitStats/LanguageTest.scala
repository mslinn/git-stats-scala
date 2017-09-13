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
    val res = fileLanguage(Paths.get("Dockerfile.2.0.0"))
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

  test("fileLanguage - Full file path, name starts with dot") {
    val res = fileLanguage(Paths.get("/opt/projects/whatever/.gitignore"))
    assert(res === "Miscellaneous", "File language")
  }

  test("fileLanguage - Full file path, does not have suffix but is known name") {
    val res = fileLanguage(Paths.get("/opt/projects/whatever/makefile.old"))
    assert(res === "Makefile", "File language")
  }

  test("fileLanguage - file does not exist") {
    val res = fileLanguage(Paths.get("/no/such/file"))
    assert(res === "Unknown", "File language")
  }

}
