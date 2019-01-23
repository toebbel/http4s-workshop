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
      case Left(MissileNotFound(id)) => Response[IO](status = NotFound).withBody(notFoundError)
      case err@(Left(MissileNotArmed(_)) | Left(MissileNotInMaintenance(_))) => Response[IO](status = MethodNotAllowed).withBody(json""" {"error": "oh no! you can't do that."} """)
      case Left(error) => Response[IO](status = InternalServerError).withBody(json""" {"error": "boom?"} """)

      case Right(content) => Response[IO](status = Ok).withBody(content.asJson)
    }
  }
}
