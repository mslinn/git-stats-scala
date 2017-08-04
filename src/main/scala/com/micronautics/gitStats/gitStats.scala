package com.micronautics

import scala.sys.process._

package object gitStats {
  def run(cmd: String*): ProcessBuilder = Process(cmd)

  def getOutputFrom(cmd: String*): String = Process(cmd).!!.trim
}
