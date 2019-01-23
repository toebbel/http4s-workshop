package com.speedledger.workshop.controllers

import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.util.CaseInsensitiveString
import org.scalatest.MustMatchers

class PlaygroundControllerTest extends org.scalatest.FreeSpec with MustMatchers {

  val client = Client.fromHttpService(PlaygroundController())

  "PlaygroundController" - {
    "GET /ping" - {
      "pongs" in {
        client.expect[String]("/ping").unsafeRunSync() mustEqual "pong"
      }
    }

    "/calc/{a}/add/{b}" - {
      "adds up two positive numbers" in {
        client.expect[String]("/calc/1/plus/2").unsafeRunSync() mustEqual "3"
      }
    }

    "/headers/{headerName}" - {
      "returns 'header not found' if header was not passed" in {
        client.expect[String]("/headers/x-spl-user").unsafeRunSync() mustEqual "header not found"
      }

      "returns value of passed header" in {
        val customHeaders = Header.Raw(CaseInsensitiveString("x-spl-user"), "42")
        val req = GET(Uri.uri("/headers/x-spl-user"), customHeaders)
        client.expect[String](req).unsafeRunSync() mustEqual "42"
      }
    }

    "/dateDiff/{dateA}/and/{dateB}" - {
      "difference in days" in {
        client.expect[String]("/dateDiff/2018-01-23/and/2018-01-24").unsafeRunSync() mustEqual "that is 1 days"
      }
    }
  }
}
