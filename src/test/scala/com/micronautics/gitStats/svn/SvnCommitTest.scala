package com.micronautics.gitStats.svn

import java.nio.charset.CodingErrorAction

import com.micronautics.gitStats.svn.SvnCommit._
import org.scalatest.FunSuite

import scala.io.{Codec, Source}

class SvnCommitTest extends FunSuite {

  test("FileModif - null file name") {
    intercept[IllegalArgumentException] {
      FileModif(null, 4)
    }
  }

  test("FileModif - empty file name") {
    intercept[IllegalArgumentException] {
      FileModif("", 4)
    }
  }



  test("commitEntriesIterator - null svn log output") {
    intercept[IllegalArgumentException] {
      commitEntriesIterator(null)
    }
  }

  test("commitEntriesIterator - empty svn log output") {
    val res = commitEntriesIterator(Iterator.empty)
    assert(res.isEmpty, "Commit entries iterator")
  }

  test("commitEntriesIterator - svn log output from a real sample") {
    val codec: Codec = Codec.UTF8.onMalformedInput(CodingErrorAction.IGNORE)
    val input = Source.fromInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log"))(codec)
    val res = commitEntriesIterator(input.getLines())
    assert(res.size === 71, "Number of commits")
  }
}
