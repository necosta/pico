package com.necosta.pico.huffman

object HuffmanOps {

  private val BitToByteRatio = 8 // 8 bits in 1 byte

  private type Offset = Byte

  val bitToByte: List[Boolean] => List[(Byte, Offset)] = {
    _.sliding(BitToByteRatio, BitToByteRatio)
      .map(boolList =>
        (
          boolList.foldLeft(0)((i, b) => (i << 1) + (if (b) 1 else 0)),
          (BitToByteRatio - boolList.size).toByte // Set offset for partial lists
        )
      )
      // Byte represents a number [-128,127]
      // bitToByte provides a number [0,255]
      // We need to shift this value to fit the byte range
      .map { case (b, o) => ((b - 128).toByte, o) }
      .toList
  }

  val byteToBit: List[Byte] => List[Boolean] = {
    case init :+ offset =>
      val booleans = init.init.flatMap(byte =>
        (0 until 8).foldLeft(List[Boolean]()) { (bs, bit) => ((((byte + 128) >> bit) & 1) == 1) +: bs }
      )
      val truncateLastBool = (0 until 8 - offset)
        .foldLeft(List[Boolean]()) { (bs, bit) => ((((init.last + 128) >> bit) & 1) == 1) +: bs }
      booleans ++ truncateLastBool
    case x => Nil
  }
}
