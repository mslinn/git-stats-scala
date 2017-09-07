package com.micronautics.gitStats

import org.scalatest.FunSuite
import Language._

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
}
