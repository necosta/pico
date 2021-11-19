package com.necosta.pico

import com.necosta.pico.Huffman.Tree

object FileCodec {

  def encode(b: Array[Byte])(tree: Tree): Array[Byte] = {
    val chars = b.map(_.toChar).toList
    val bits  = HuffmanCodec.encode(tree)(chars)
    HuffmanOps.bitToByte.apply(bits).toArray
  }

  def decode(b: Array[Byte])(tree: Tree): Array[Byte] = {
    val boolList = HuffmanOps.byteToBit.apply(b.toList)
    val bits     = HuffmanCodec.decode(tree)(boolList)
    bits.map(_.toByte).toArray
  }
}
