package com.necosta.pico

import cats.effect.{ExitCode, IO, IOApp}
import com.necosta.pico.file.FileOps

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- validateArgs(args)
      fileOps = new FileOps(args.head)
      bytesCount <- fileOps.compress(fileOps.sourceFile)
      _          <- IO.println(s"${bytesCount.readCount} bytes read from ${fileOps.sourceFile.getPath}")
      _          <- IO.println(s"${bytesCount.writeCount} bytes wrote into ${fileOps.targetFile.getPath}")
    } yield ExitCode.Success

  private def validateArgs(args: List[String]): IO[Unit] = {
    if (args.length < 1) {
      IO.raiseError(new IllegalArgumentException("A source file is required as parameter."))
    } else IO.unit
  }
}
