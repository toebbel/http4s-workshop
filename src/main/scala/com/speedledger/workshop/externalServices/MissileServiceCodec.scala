package com.speedledger.workshop.externalServices

import MissileStatus.MissileStatus
import io.circe.generic.semiauto._
import io.circe.{Encoder, _}

trait MissileServiceCodec {

  implicit val missileEncoder: Encoder[Missile] = deriveEncoder[Missile]
  implicit val missilesEncoder: Encoder[List[Missile]] = Encoder.encodeList[Missile]
  implicit val genderEncoder: Encoder[MissileStatus.Value] = Encoder.enumEncoder(MissileStatus)
  implicit val missileServiceErrorEncoder: Encoder[MissileServiceError] = (a: MissileServiceError) => Json.obj(
    "error" -> Json.fromString(a.getClass.getSimpleName),
  )

  implicit val missileServiceNotFoundErrorEncoder: Encoder[MissileNotFound] = (a: MissileNotFound) => Json.obj(
    "error" -> Json.fromString(a.getClass.getSimpleName),
  )
}
