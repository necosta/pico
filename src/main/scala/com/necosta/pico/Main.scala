package com.necosta.pico

import cats.effect.{Clock, ExitCode, IO, IOApp}
import com.necosta.pico.file.FileOps

object Main extends IOApp {

  implicit private val clock = Clock[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- validateArgs(args)
      fileOps = new FileOps(args.head)
      start <- clock.monotonic
      bc    <- fileOps.compress()
      stop  <- clock.monotonic
      _     <- IO.println(s"${bc.readCount} bytes read from ${fileOps.sourceFile.getPath}")
      _     <- IO.println(s"${bc.writeCount} bytes wrote into ${fileOps.targetFile.getPath}")
      ratio = if (bc.writeCount == 0) -1 else bc.readCount * 1.0 / bc.writeCount
      _ <- IO.println(f"Compression ratio: $ratio%2.2f")
      _ <- IO.println(s"Time spent: ${(stop - start).toSeconds} seconds")
    } yield ExitCode.Success

  private def validateArgs(args: List[String]): IO[Unit] = {
    if (args.length < 1) {
      IO.raiseError(new IllegalArgumentException("A source file is required as parameter."))
    } else IO.unit
  }
}
