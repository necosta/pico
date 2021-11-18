package com.necosta.pico

import cats.effect.testing.specs2.CatsEffect
import org.specs2.mutable.Specification

class HuffmanOpsSpec extends Specification with CatsEffect {

  import HuffmanOps._

  "HuffmanOps" should {
    "convert 8 0's into value 0" in {
      val res = List(List.fill(8)(false)).flatMap(bitToByte)
      res.head mustEqual 0
    }
    "convert 8 1's into value 255" in {
      val res = List(List.fill(8)(true)).flatMap(bitToByte)
      res.head mustEqual 255
    }
    "convert 4 1's and 4 0's into value 112" in {
      val res = List(List.fill(4)(true) ::: List.fill(4)(false)).flatMap(bitToByte)
      res.head mustEqual 240
    }
  }
}
