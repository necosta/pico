package com.necosta.pico

import com.necosta.pico.Huffman.Tree

object FileCodec {

  def encode(b: Array[Byte])(tree: Tree): Array[Byte] = {
    val bits = HuffmanCodec.encode(tree)(b.toList)
    HuffmanOps.bitToByte.apply(bits).toArray
  }

  def decode(b: Array[Byte])(tree: Tree): Array[Byte] = {
    val boolList = HuffmanOps.byteToBit.apply(b.toList)
    HuffmanCodec.decode(tree)(boolList).toArray
  }
}
