package com.necosta.pico.cli

import cats.free.Free
import cats.syntax.all.*
import io.github.vigoo.clipp.Parameter
import java.io.File
import io.github.vigoo.clipp.syntax.*
import io.github.vigoo.clipp.parsers.*

final case class CLIParameters(
    compressionOption: CompressionOption,
    inputFile: File,
    chunkSize: Option[Int]
)

object CLIParameters {

  val paramSpec: Free[Parameter, CLIParameters] = for {
    _           <- metadata("pico-app")
    commandName <- command("compress", "decompress")
    compressionOption <- liftEither[String, CompressionOption]("Invalid command", Compress)(
      CompressionOption.fromString(commandName)
    )
    file <- namedParameter[File](
      description = "Input file",
      placeholder = "file",
      shortName = 'f',
      longNames = "file"
    )
    chunkSize <- optional(
      namedParameter[Int](
        description = "Chunk size (in kb)",
        placeholder = "value",
        shortName = 'c',
        longNames = "chunkSize"
      )
    )
    validatedChunkSize <- liftEither[String, Option[Int]]("Invalid chunk size value", 10.some)(
      validateChunkSize(compressionOption, chunkSize)
    )
  } yield CLIParameters(compressionOption, file, validatedChunkSize)

  private def validateChunkSize(
      compressionOption: CompressionOption,
      chunkSize: Option[Int]
  ): Either[String, Option[Int]] = {
    (compressionOption, chunkSize) match {
      case (_, None)                          => Right(None)
      case (Compress, Some(size)) if size > 0 => Right(Some(size))
      case (Compress, Some(_))                => Left("Chunk size has to be set as a positive number")
      case _                                  => Left("Chunk size only applies to compress command")
    }
  }
}
