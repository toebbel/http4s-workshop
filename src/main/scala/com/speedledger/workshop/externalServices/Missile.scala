package com.speedledger.workshop.externalServices

import com.speedledger.workshop.externalServices.MissileStatus.MissileStatus


object MissileStatus extends Enumeration {
  type MissileStatus = Value
  val Maintenance, Armed, MidAir, Exploded = Value
}

case class Missile(missileId: String, status: MissileStatus)