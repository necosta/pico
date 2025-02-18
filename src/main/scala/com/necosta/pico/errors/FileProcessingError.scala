package com.necosta.pico.errors

import scala.util.control.NoStackTrace

sealed trait FileProcessingError extends RuntimeException with NoStackTrace

case class CompressionError(msg: String) extends FileProcessingError {
  override def toString: String = s"CompressionError: $msg"
}

case class DecompressionError(msg: String) extends FileProcessingError {
  override def toString: String = s"DecompressionError: $msg"
}
