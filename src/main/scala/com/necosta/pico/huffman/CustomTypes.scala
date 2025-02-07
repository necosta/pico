package com.necosta.pico.huffman

import cats.data.ValidatedNec

trait CustomTypes {
  // A byte maps to a list of bits
  type Table = Map[Byte, List[Boolean]]

  // Validated data type for encoding: Error -> byte ; Success -> list of bits
  type BitsV = ValidatedNec[Byte, List[Boolean]]

  // Validated data type for decoding: Error -> list of bits ; Success -> list of bytes
  type ByteV = ValidatedNec[List[Boolean], List[Byte]]
}
