package com.necosta.pico

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {

  def run: IO[Unit] = for {
    _ <- IO.println("Welcome to Pico - v2")
  } yield ()
}
