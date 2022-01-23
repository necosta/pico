package com.necosta.pico

sealed trait CodecOption
final case object Compress   extends CodecOption
final case object Decompress extends CodecOption

object CodecOption {
  def fromString(input: String): CodecOption = input match {
    case "compress"   => Compress
    case "decompress" => Decompress
  }
}
