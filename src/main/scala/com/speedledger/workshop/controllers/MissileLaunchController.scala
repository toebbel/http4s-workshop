package com.speedledger.workshop.controllers

import cats.data._
import cats.effect._
import com.speedledger.workshop.externalServices._
import org.http4s._
import org.http4s.dsl.io._
import io.circe.syntax._
import io.circe.literal._

object MissileLaunchController extends MissileServiceCodec {
  def apply(missileService: MissileService) = HttpService[IO] {
    case GET -> Root / "missiles" / missileId => {
      missileService.findMissile(missileId)
    }
    case PUT -> Root / "missiles" / missileId / "arm" => {
      missileService.armMissile(missileId)
    }
    case DELETE -> Root / "missiles" / missileId / "arm" => {
      missileService.disarmMissile(missileId)
    }
    case POST -> Root / "missiles" / missileId / "fire" => {
      missileService.fireMissile(missileId)
    }
    case GET -> Root / "missiles" => {
      missileService.listMissiles()
    }
    case _ => {
      IO(Response[IO](status = NotFound))
    }
  }

  implicit def EitherToResponse[T](result: EitherT[IO, MissileServiceError, T])(implicit encoder: io.circe.Encoder[T]): IO[Response[IO]] = {
    import org.http4s.circe.CirceEntityEncoder._

    val notFoundError = json""" {"error": "missile not found"} """
    result.value.flatMap {
      case Right(content) => Response[IO](status = Ok).withBody(content.asJson)

        // MissileNotFound must return 404
        // MissileNotInMaintenance must return MethodNotAllowed (405)
        // MissileNotInMaintenance must return MethodNotAllowed (405)
      case _ => ???
    }
  }
}
