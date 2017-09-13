package com.micronautics.gitStats.svn

import com.micronautics.gitStats.svn.SvnUsers._
import org.scalatest.FunSuite

class SvnUsersTest extends FunSuite {

  test("parseUserNames - empty svn auth output") {
    val res = parseUserNames("")
    assert(res === Set(), "User names")
  }

  test("parseUserNames - garbage svn auth output") {
    val res = parseUserNames("""Some
                             |garbage""".stripMargin)
    assert(res === Set(), "User names")
  }

  test("parseUserNames - one entry in svn auth output") {
    val svnAuthOutput = """------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn.host.domain.com:18580> Corporate Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname.lastname
                          |
                          |Credentials cache in '/home/user/.subversion' contains 1 credentials""".stripMargin
    val res = parseUserNames(svnAuthOutput)
    assert(res === Set("firstname.lastname"), "User names")
  }

  test("parseUserNames - two identical entries in svn auth output") {
    val svnAuthOutput = """------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn.host.domain.com:18580> Corporate Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname.lastname
                          |
                          |------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn-other.host.domain.com:18180> Other Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname.lastname
                          |
                          |Credentials cache in '/home/user/.subversion' contains 2 credentials""".stripMargin
    val res = parseUserNames(svnAuthOutput)
    assert(res === Set("firstname.lastname"), "User names")
  }

  test("parseUserNames - two different entries in svn auth output") {
    val svnAuthOutput = """------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn.host.domain.com:18580> Corporate Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname1.lastname1
                          |
                          |------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn-other.host.domain.com:18180> Other Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname2.lastname2
                          |
                          |Credentials cache in '/home/user/.subversion' contains 2 credentials""".stripMargin
    val res = parseUserNames(svnAuthOutput)
    assert(res === Set("firstname1.lastname1", "firstname2.lastname2"), "User names")
  }
}
