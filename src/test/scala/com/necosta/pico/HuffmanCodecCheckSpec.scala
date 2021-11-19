package com.necosta.pico

import com.necosta.pico.Huffman.{Fork, Leaf}
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties}

class HuffmanCodecCheckSpec extends Properties("HuffmanCodec") {

  import HuffmanCodec._

  property("encode text given leaf node") = forAll(Gen.alphaChar, Gen.choose(0, 100)) {
    (char, weight) =>
      val leaf = Leaf(char.toByte, weight)
      val text = List.fill(3)(char.toByte)
      encode(leaf)(text) == List(true, true, true)
  }

  property("encode text given fork node") = forAll(Gen.pick(3, 'a' to 'z')) { chars =>
    {
      val bytes = chars.map(_.toByte)
      val leaf1 = Leaf(bytes.head, 100) // 1
      val leaf2 = Leaf(bytes(1), 10)    // 0, 1
      val leaf3 = Leaf(bytes.last, 1)   // 0, 0
      val fork  = Fork(leaf1, Fork(leaf2, leaf3))
      encode(fork)(bytes.toList) == List(true, false, true, false, false)
    }
  }

  property("decode bits given leaf node") = forAll(Gen.alphaChar, Gen.choose(0, 100)) {
    (char, weight) =>
      val leaf = Leaf(char.toByte, weight)
      val bits = List.fill(3)(true)
      decode(leaf)(bits) == List.fill(3)(char.toByte)
  }

  property("decode bits given fork node") = forAll(Gen.pick(3, 'a' to 'z')) { chars =>
    {
      val bytes = chars.map(_.toByte)
      val leaf1 = Leaf(bytes.head, 100) // 1
      val leaf2 = Leaf(bytes(1), 10)    // 0, 1
      val leaf3 = Leaf(bytes.last, 1)   // 0, 0
      val fork  = Fork(leaf1, Fork(leaf2, leaf3))
      val bits  = List(true, false, true, false, false)
      decode(fork)(bits) == bytes.toList
    }
  }
}
