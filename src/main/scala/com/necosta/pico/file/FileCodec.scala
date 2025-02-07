package com.necosta.pico.file

import com.necosta.pico.huffman.Huffman.*

object FileCodec {

  def encode(b: List[Byte])(tree: HuffmanTree): Either[String, List[Byte]] = ???

  def decode(b: List[Byte])(tree: HuffmanTree): Either[String, List[Byte]] = ???
}
