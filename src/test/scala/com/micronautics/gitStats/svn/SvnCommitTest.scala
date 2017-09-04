package com.micronautics.gitStats.svn

import org.scalatest.FunSuite

class SvnCommitTest extends FunSuite {

  test("Null file name") {
    intercept[IllegalArgumentException] {
      SvnCommit(null, 4)
    }
  }

  test("Empty file name") {
    intercept[IllegalArgumentException] {
      SvnCommit("", 4)
    }
  }
}
