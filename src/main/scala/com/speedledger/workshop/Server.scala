package com.speedledger.workshop

import cats.effect.IO
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze._
import com.speedledger.workshop.controllers.{PlaygroundController, MissileLaunchController}
import com.speedledger.workshop.externalServices.MissileService

import scala.concurrent.ExecutionContext.Implicits.global


object Main extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService((MissileLaunchController)(new MissileService()))
      .mountService(PlaygroundController())
      .serve
}
