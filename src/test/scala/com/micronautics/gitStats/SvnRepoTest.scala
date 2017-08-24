package com.micronautics.gitStats

import org.scalatest.FunSuite
import SvnRepo._

class SvnRepoTest extends FunSuite {

  test("getUserName - empty svn auth output") {
    val res = getUserNames("")
    assert(res === Set(), "User names")
  }

  test("getUserName - one entry in svn auth output") {
    val svnAuthOutput = """------------------------------------------------------------------------
                          |Credential kind: svn.simple
                          |Authentication realm: <https://svn.host.domain.com:18580> Corporate Authorization Realm
                          |Password cache: gnome-keyring
                          |Username: firstname.lastname
                          |
                          |Credentials cache in '/home/user/.subversion' contains 1 credentials""".stripMargin
    val res = getUserNames(svnAuthOutput)
    assert(res === Set("firstname.lastname"), "User names")
  }

  test("getUserName - two identical entries in svn auth output") {
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
    val res = getUserNames(svnAuthOutput)
    assert(res === Set("firstname.lastname"), "User names")
  }

  test("getUserName - two different entries in svn auth output") {
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
    val res = getUserNames(svnAuthOutput)
    assert(res === Set("firstname1.lastname1", "firstname2.lastname2"), "User names")
  }
}
