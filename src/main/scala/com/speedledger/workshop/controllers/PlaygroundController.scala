package com.speedledger.workshop.controllers

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.util.CaseInsensitiveString

object PlaygroundController {
  def apply() = HttpService[IO] {
      // task 0: make this route return the string "pong"
    case GET -> Root / "ping" => Ok("pong")

      // task 1: add a route on "calc/{a}/plus/{b}" that returns the sum of a and b as a string
    case GET -> Root / "calc" / IntVar(a) / "plus" / IntVar(b) => Ok(s"${a + b}")

      // task 2: add a route on "/headers/{headerName}" that returns either the value of the header, or the string "header not found"
    case req@GET -> Root / "headers" / headerName => Ok(req.headers.get(CaseInsensitiveString(headerName)).map(_.value).getOrElse("header not found"))
    case req@GET -> Root / "headers" / headerName => Ok(req.headers.mkString(", "))

      // this helps us to understand if tests fail
    case req => NotFound(s"could not find ${req.method} ${req.uri.renderString}")
  }
}
