package com.necosta.pico

import java.io.File

import io.github.vigoo.clipp.syntax._
import io.github.vigoo.clipp.parsers._

final case class CLIParameters(
    codecOption: CodecOption,
    inputFile: File
)

object CLIParameters {
  val paramSpec = for {
    _           <- metadata("pico-app")
    commandName <- command("compress", "decompress")
    codecOption = CodecOption.fromString(commandName)
    file <- namedParameter[File](
      "Input file",
      "file",
      "file"
    )
  } yield CLIParameters(codecOption, file)
}
