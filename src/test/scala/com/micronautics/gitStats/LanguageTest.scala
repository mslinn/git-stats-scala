package com.micronautics.gitStats

import java.nio.file.Paths

import com.micronautics.gitStats.Language._
import org.scalatest.FunSuite

class LanguageTest extends FunSuite {

  test("fileSuffix - file name is null") {
    intercept[IllegalArgumentException] {
      fileSuffix(null)
    }
  }

  test("fileSuffix - file name is empty") {
    val res = fileSuffix("")
    assert(res === None, "File suffix")
  }

  test("fileSuffix - file name is dot") {
    val res = fileSuffix(".")
    assert(res === Some(""), "File suffix")
  }

  test("fileSuffix - file name starts with dot") {
    val res = fileSuffix(".gitignore")
    assert(res === Some("gitignore"), "File suffix")
  }

  test("fileSuffix - file name does not have dot") {
    val res = fileSuffix("Dockerfile")
    assert(res === None, "File suffix")
  }

  test("fileSuffix - file name has dot") {
    val res = fileSuffix("Test.groovy")
    assert(res === Some("groovy"), "File suffix")
  }

  test("fileSuffix - file name has more than one dot") {
    val res = fileSuffix("Test.groovy.bkp")
    assert(res === Some("bkp"), "File suffix")
  }



  test("fileLanguage - file name is null") {
    intercept[IllegalArgumentException] {
      fileLanguage(null)
    }
  }

  test("fileLanguage - file name is empty") {
    intercept[IllegalArgumentException] {
      fileLanguage("")
    }
  }

  test("fileLanguage - file name has known suffix") {
    val res = fileLanguage("Test.scala")
    assert(res === "Scala", "File language")
  }

  test("fileLanguage - file name does not have suffix but has known name") {
    val res = fileLanguage("Dockerfile")
    assert(res === "Dockerfile", "File language")
  }

  test("fileLanguage - file name starts with dot") {
    val res = fileLanguage(".gitignore")
    assert(res === "Miscellaneous", "File language")
  }

  test("fileLanguage - file does not have suffix but its content is Shell") {
    val path = Paths.get(sys.props("user.dir"), "bin", "run")
    if (path.toFile.canRead) {
      val res = fileLanguage(path.toString)
      assert(res === "Shell", "File language")
    }
  }

  test("fileLanguage - file does not exist") {
    val res = fileLanguage("/no/such/file")
    assert(res === "Unknown", "File language")
  }

}
