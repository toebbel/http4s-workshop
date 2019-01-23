package com.speedledger.workshop.externalServices

import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import com.speedledger.workshop.externalServices.MissileStatus.MissileStatus

import scala.collection.mutable

sealed trait MissileServiceError
case class MissileNotFound(missileId: String) extends MissileServiceError
case class MissileNotArmed(missileId: String) extends MissileServiceError
case class MissileNotInMaintenance(missileId: String) extends MissileServiceError

object MissileService {
  private val defaultData = Map(
    "dirty-harry" -> MissileStatus.Maintenance,
    "gonzo-bonzo" -> MissileStatus.Maintenance,
    "fox-paradox" -> MissileStatus.Maintenance
  )

  def apply(input: Map[String, MissileStatus] = defaultData) = {
    val result = new MissileService()
    result.dataStore = mutable.Map() ++ input.map{ case (key, status) => key -> Missile(key, status)}
    result
  }
}

class MissileService {
  type Result = EitherT[IO, MissileServiceError, Missile]

  //mutable data :O - don't do this at home
  var dataStore: mutable.Map[String, Missile] = mutable.Map()

  def listMissiles(): EitherT[IO, MissileServiceError, List[Missile]] =
    EitherT.rightT[IO, MissileServiceError](dataStore.values.toList)

  def findMissile(missileId: String): Result = {
    EitherT.fromOption(dataStore.get(missileId), MissileNotFound(missileId))
  }

  def armMissile(missileId: String): Result = changeState(missileId){
    missile =>
      if(missile.status != MissileStatus.Maintenance)
        MissileNotInMaintenance(missileId).asLeft[Missile]
      else
        missile.copy(status = MissileStatus.Armed).asRight[MissileServiceError]
    }

  def disarmMissile(missileId: String): Result = changeState(missileId){
    missile =>
      if(missile.status != MissileStatus.Armed)
        MissileNotArmed(missileId).asLeft[Missile]
      else
        missile.copy(status = MissileStatus.Maintenance).asRight[MissileServiceError]
  }

  def fireMissile(missileId: String): Result = changeState(missileId){
    missile =>
      if(missile.status != MissileStatus.Armed)
        MissileNotArmed(missileId).asLeft[Missile]
      else
        missile.copy(status = MissileStatus.MidAir).asRight[MissileServiceError]
  }


  private def changeState(missileId: String)(f: Missile => Either[MissileServiceError, Missile]): Result = {
    for {
      missile <- findMissile(missileId)
      updated <- EitherT.fromEither[IO](f(missile))
    } yield {
      dataStore.put(missileId, updated)
      updated
    }
  }
}
