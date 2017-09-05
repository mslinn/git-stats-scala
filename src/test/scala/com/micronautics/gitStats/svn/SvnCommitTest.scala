package com.micronautics.gitStats.svn

import java.io.FilterInputStream

import org.scalatest.FunSuite
import SvnCommit._

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
    val stream = new FilterInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log")) {
      override def read(): Int = {
        val b = super.read()

        b
      }
    }
    val input = Source.fromInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log"))//(Codec("windows-1252"))
    val res = commitEntriesIterator(input.getLines())
    assert(!res.isEmpty, "Commit entries iterator")
    var i = 0
    while (res.hasNext) {
      println(s"----------------- $i")
      println(res.next().mkString("\n"))
      println()
      i = i +1
    }

//    assert(res.size === 5511, "Number of commits")
  }
}
