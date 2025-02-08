package com.necosta.pico.cli

import cats.free.Free
import com.necosta.pico.cli.CompressionOption

import io.github.vigoo.clipp.Parameter
import java.io.File
import io.github.vigoo.clipp.syntax.*
import io.github.vigoo.clipp.parsers.*

final case class CLIParameters(
    compressionOption: CompressionOption,
    inputFile: File
)

object CLIParameters {
  val paramSpec: Free[Parameter, CLIParameters] = for {
    _           <- metadata("pico-app")
    commandName <- command("compress", "decompress")
    codecOption = CompressionOption.fromString(commandName)
    file <- namedParameter[File](
      description = "Input file",
      placeholder = "file",
      shortName = 'f',
      longNames = "file"
    )
  } yield CLIParameters(codecOption, file)
}
