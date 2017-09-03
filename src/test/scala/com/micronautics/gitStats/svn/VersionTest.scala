package com.micronautics.gitStats.svn

import com.micronautics.gitStats.Version
import com.micronautics.gitStats.Version._
import org.scalatest.FunSuite

class VersionTest extends FunSuite {

  test("null major") {
    intercept[IllegalArgumentException] {
      Version(null)
    }
  }

  test("empty string major") {
    intercept[IllegalArgumentException] {
      Version("")
    }
  }



  test("compare - 0 and 0") {
    val v1 = Version("0")
    val v2 = Version("0")
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 0 and 1") {
    val v1 = Version("0")
    val v2 = Version("1")
    val res = v1 compare v2
    assert(res === -1, "compare result")
  }

  test("compare - 1 and 0") {
    val v1 = Version("1")
    val v2 = Version("0")
    val res = v1 compare v2
    assert(res === 1, "compare result")
  }

  test("compare - 1.1 and 1.0") {
    val v1 = Version("1", "1")
    val v2 = Version("1", "0")
    val res = v1 compare v2
    assert(res === 1, "compare result")
  }

  test("compare - 1.0 and 1.1") {
    val v1 = Version("1", "0")
    val v2 = Version("1", "1")
    val res = v1 compare v2
    assert(res === -1, "compare result")
  }

  test("compare - 1 and 1.0") {
    val v1 = Version("1")
    val v2 = Version("1", "0")
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 1.0 and 1") {
    val v1 = Version("1", "0")
    val v2 = Version("1")
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 1.2.3-u1 and 1.2.3") {
    val v1 = Version("1", "2", "3-u1")
    val v2 = Version("1", "2", "3")
    val res = v1 compare v2
    assert(res === 1, "compare result")
  }


  test("parse - null") {
    intercept[IllegalArgumentException] {
      parse(null)
    }
  }

  test("parse - empty string") {
    intercept[IllegalArgumentException] {
      parse("")
    }
  }

  test("parse - 1") {
    val res = parse("1")
    assert(res === Version("1"), "version")
  }

  test("parse - 1.0") {
    val res = parse("1.0")
    assert(res === Version("1", "0"), "version")
  }

  test("parse - 1.2.3-u1") {
    val res = parse("1.2.3-u1")
    assert(res === Version("1", "2", "3-u1"), "version")
  }
}
