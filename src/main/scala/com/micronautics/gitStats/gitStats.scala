package com.micronautics

import org.slf4j.Logger
import scala.sys.process._

package object gitStats {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("gitstats")

  def run(cmd: String*): ProcessBuilder = {
    logger.info(cmd.mkString(" "))
    Process(cmd)
  }

  def getOutputFrom(cmd: String*): String = Process(cmd).!!.trim
}
