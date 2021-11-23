package com.necosta.pico.huffman

import cats.effect.testing.specs2.CatsEffect
import org.specs2.mutable.Specification

class HuffmanOpsSpec extends Specification with CatsEffect {

  import HuffmanOps._

  "HuffmanOps" should {
    "convert 8 0's into byte min value" in {
      val res = List(List.fill(8)(false)).flatMap(bitToByte)
      res.head mustEqual Byte.MinValue
    }
    "convert 8 1's into byte max value" in {
      val res = List(List.fill(8)(true)).flatMap(bitToByte)
      res.head mustEqual Byte.MaxValue
    }
    "convert 4 1's and 4 0's into value 240 (-128)" in {
      val res = List(List.fill(4)(true) ::: List.fill(4)(false)).flatMap(bitToByte)
      res.head mustEqual 112
    }
    "convert byte min value into list of false booleans" in {
      val res = byteToBit.apply(List(Byte.MinValue))
      res mustEqual List.fill(8)(false)
    }
    "convert byte max value into list of true booleans" in {
      val res = byteToBit.apply(List(Byte.MaxValue))
      res mustEqual List.fill(8)(true)
    }
    "convert value 1 (-128) into binary" in {
      val res = byteToBit.apply(List((-127).toByte))
      res mustEqual List.fill(7)(false) ::: List.fill(1)(true)
    }
    "convert value 240 (-128) into binary" in {
      val res = byteToBit.apply(List(112.toByte))
      res mustEqual List.fill(4)(true) ::: List.fill(4)(false)
    }
    "convert 2 max value bytes " in {
      val res = byteToBit.apply(List(Byte.MaxValue, Byte.MaxValue))
      res mustEqual List.fill(16)(true)
    }
  }
}
