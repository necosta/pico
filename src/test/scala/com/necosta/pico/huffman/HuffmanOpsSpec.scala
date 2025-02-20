package com.necosta.pico.huffman

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HuffmanOpsSpec extends AnyFlatSpec with Matchers {
  "HuffmanOps" should "map bits to bytes" in {
    val inputByte = 'a'.toByte
    val input     = inputByte.toInt.toBinaryString.toList.map(_ == '1')
    val res       = HuffmanOps.bitToByte(input)
    res shouldBe List((inputByte - 128, 1))
  }

  "HuffmanOps" should "map odd number of bits to bytes and set offset" in {
    val input = List[Boolean](true, false, true) // 4 + 0 + 1
    val res   = HuffmanOps.bitToByte(input)
    res shouldBe List((5 - 128, 5))
  }
}
