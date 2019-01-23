package com.speedledger.workshop.controllers

import cats.effect.IO
import com.speedledger.workshop.externalServices.MissileStatus.MissileStatus
import com.speedledger.workshop.externalServices.{MissileService, MissileStatus}
import org.http4s.Method._
import org.http4s._
import org.http4s.client.Client
import org.scalatest.MustMatchers

class MissileLaunchControllerTest
    extends org.scalatest.FreeSpec
    with MustMatchers {

  def candidatewithData(data: Map[String, MissileStatus]) = {
    val candidate = MissileLaunchController(MissileService(data))
    Client.fromHttpService(candidate)
  }

  "MissileLaunchControllerTest" - {
    "GET missile" - {
      "with no missiles" - {
        val client = candidatewithData(Map.empty)

        "returns 404" in {
          val request = Request[IO](uri = Uri.uri("/missiles/dirty-harry"))
          val responseStatus =
            client.fetch(request)(rsp => IO(rsp.status)).unsafeRunSync()
          responseStatus mustEqual Status.NotFound
        }
      }

      "with two missiles" - {
        val client = candidatewithData(
          Map("dirty-harry" -> MissileStatus.Maintenance,
            "bravo-charlie" -> MissileStatus.MidAir))

        "returns correct missile" in {
          val request = Request[IO](uri = Uri.uri("/missiles/dirty-harry"))
          val responseJson = client.expect[String](request).unsafeRunSync()
          responseJson mustEqual """{"missileId":"dirty-harry","status":"Maintenance"}"""
        }
      }
    }

    "missile in maintenance" - {
      "PUT missile/{id}/arm" - {
        "becomes armed" in {
          val client = candidatewithData(Map("dirty-harry" -> MissileStatus.Maintenance))
          val request =
            Request[IO](method = PUT,
                        uri = Uri.uri("/missiles/dirty-harry/arm"))
          val responseJson = client.expect[String](request).unsafeRunSync()
          responseJson mustEqual """{"missileId":"dirty-harry","status":"Armed"}"""
        }
      }

      "DELETE missile/{id}/arm" - {
        "returns error" in {
          val client = candidatewithData(Map("dirty-harry" -> MissileStatus.Maintenance))
          val request =
            Request[IO](method = DELETE,
                        uri = Uri.uri("/missiles/dirty-harry/arm"))
          val responseStatus =
            client.fetch(request)(rsp => IO(rsp.status)).unsafeRunSync()
          responseStatus mustEqual Status.MethodNotAllowed
        }
      }
    }

    "missile is armed" - {
      "PUT missile/{id}/arm" - {
        "returns error" in {
          val client = candidatewithData(Map("dirty-harry" -> MissileStatus.Armed))
          val request =
            Request[IO](method = PUT,
              uri = Uri.uri("/missiles/dirty-harry/arm"))
          val responseStatus =
            client.fetch(request)(rsp => IO(rsp.status)).unsafeRunSync()
          responseStatus mustEqual Status.MethodNotAllowed
        }
      }

      "DELETE missile/{id}/arm" - {
        "returns to maintenance" in {
          val client = candidatewithData(Map("dirty-harry" -> MissileStatus.Armed))
          val request =
            Request[IO](method = DELETE,
              uri = Uri.uri("/missiles/dirty-harry/arm"))
          val responseJson = client.expect[String](request).unsafeRunSync()
          responseJson mustEqual """{"missileId":"dirty-harry","status":"Maintenance"}"""
        }
      }

      "POST missile/{id}/fire" - {
        "becomes midAir" in {
          val client = candidatewithData(Map("dirty-harry" -> MissileStatus.Armed))
          val request =
            Request[IO](method = POST,
              uri = Uri.uri("/missiles/dirty-harry/fire"))
          val responseJson = client.expect[String](request).unsafeRunSync()
          responseJson mustEqual """{"missileId":"dirty-harry","status":"MidAir"}"""
        }
      }
    }
  }
}
