package com.speedledger.workshop.controllers

import java.time.{Duration, LocalDate}

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

object PlaygroundController {

  def dateDiff(dateA: LocalDate, dateB: LocalDate) = IO(Duration.between(dateA.atStartOfDay(), dateB.atStartOfDay()).toDays())


  def apply() = HttpService[IO] {
    // task 0: make this route return the string "pong"
    case GET -> Root / "ping" => Ok("pong")

    // task 1: add a route on "calc/{a}/plus/{b}" that returns the sum of a and b as a string

    // task 2: add a route on "/headers/{headerName}" that returns either the value of the header, or the string "header not found"

    // task 3: add a route on "/dateDiff/{dateA}/and/{dateB}" that returns number of days between dates in s"that is $days days"

    // this helps us to understand if tests fail
    case req => NotFound(s"could not find ${req.method} ${req.uri.renderString}")
  }
}
