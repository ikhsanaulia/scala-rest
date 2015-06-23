package com.ikhsan.scala.rest

package regex {
  object Email {
    def isValidFormat(email: String): Boolean = """\b[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*\b""".r.unapplySeq(email).isDefined
  }
}