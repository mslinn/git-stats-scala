package com.micronautics.gitStats.svn

import com.micronautics.gitStats.Version
import com.micronautics.gitStats.Version._
import org.scalatest.FunSuite

class VersionTest extends FunSuite {

  test("compare - 0 and 0") {
    val v1 = Version(0)
    val v2 = Version(0)
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 0 and 1") {
    val v1 = Version(0)
    val v2 = Version(1)
    val res = v1 compare v2
    assert(res === -1, "compare result")
  }

  test("compare - 1 and 0") {
    val v1 = Version(1)
    val v2 = Version(0)
    val res = v1 compare v2
    assert(res === 1, "compare result")
  }

  test("compare - 1.1 and 1.0") {
    val v1 = Version(1, 1)
    val v2 = Version(1, 0)
    val res = v1 compare v2
    assert(res === 1, "compare result")
  }

  test("compare - 1.0 and 1.1") {
    val v1 = Version(1, 0)
    val v2 = Version(1, 1)
    val res = v1 compare v2
    assert(res === -1, "compare result")
  }

  test("compare - 1 and 1.0") {
    val v1 = Version(1)
    val v2 = Version(1, 0)
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 1.0 and 1") {
    val v1 = Version(1, 0)
    val v2 = Version(1)
    val res = v1 compare v2
    assert(res === 0, "compare result")
  }

  test("compare - 1.9.5 and 1.10") {
    val v1 = Version(1, 9, 5)
    val v2 = Version(1, 10)
    val res = v1 compare v2
    assert(res === -1, "compare result")
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

  test("parse - major is not a number") {
    intercept[NumberFormatException] {
      parse("1v.2.3")
    }
  }

  test("parse - 1") {
    val res = parse("1")
    assert(res === Version(1), "version")
  }

  test("parse - 1.0") {
    val res = parse("1.0")
    assert(res === Version(1, 0), "version")
  }

  test("parse - 1.2.3-u1") {
    val res = parse("1.2.3-u1")
    assert(res === Version(1, 2, 3), "version")
  }

  test("parse - 1.2.u1") {
    val res = parse("1.2.u1")
    assert(res === Version(1, 2, 0), "version")
  }
}
