package com.necosta.pico.cli

sealed trait CompressionOption
case object Compress   extends CompressionOption
case object Decompress extends CompressionOption

object CompressionOption {
  def fromString(input: String): Either[String, CompressionOption] = input.toLowerCase match {
    case "compress"   => Right(Compress)
    case "decompress" => Right(Decompress)
    case v            => Left(v)
  }
}
