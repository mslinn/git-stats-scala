package com.micronautics.gitStats

import scala.language.reflectiveCalls

//TODO What is the use of this? Close resources in Commit.contents? Unused at present.
object Constructs {
  type Closeable = { def close(): Unit }

  def using[A <: Closeable, B](closeable: A)(f: A => B): B = {
    try {
      f(closeable)
    } finally {
      try {
        closeable.close()
      } catch { case _: Throwable => () }
    }
  }
}
