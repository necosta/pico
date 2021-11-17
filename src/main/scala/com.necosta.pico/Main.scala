package com.necosta.pico

import cats.effect.{ExitCode, IO, IOApp}

import java.io.File

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- validateArgs(args)
      sourceFile = new File(args.head)
      count <- FileOps.read(sourceFile)
      _     <- IO.println(s"$count bytes copied from ${sourceFile.getPath}")
    } yield ExitCode.Success

  private def validateArgs(args: List[String]): IO[Unit] = {
    if (args.length < 1) {
      IO.raiseError(new IllegalArgumentException("A source file is required as parameter."))
    } else IO.unit
  }
}
