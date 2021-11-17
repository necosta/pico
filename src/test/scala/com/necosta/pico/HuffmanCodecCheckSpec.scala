package com.necosta.pico

import com.necosta.pico.Huffman.{Fork, Leaf}
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Gen, Properties}

class HuffmanCodecCheckSpec extends Properties("HuffmanCodec") {

  import HuffmanCodec._

  property("encode text given leaf node") = forAll(Gen.alphaChar, Gen.choose(0, 100)) {
    (char, weight) =>
      val leaf = Leaf(char, weight)
      val text = List.fill(3)(char)
      encode(leaf)(text) == List(true, true, true)
  }

  property("encode text given fork node") = forAll(Gen.pick(3, 'a' to 'z')) {
    chars => {
      val leaf1 = Leaf(chars.head, 100) // 1
      val leaf2 = Leaf(chars(1), 10) // 0, 1
      val leaf3 = Leaf(chars.last, 1) // 0, 0
      val fork = Fork(leaf1, Fork(leaf2, leaf3))
      encode(fork)(chars.toList) == List(true, false, true, false, false)
    }
  }

  property("decode bits given leaf node") = forAll(Gen.alphaChar, Gen.choose(0, 100)) {
    (char, weight) =>
      val leaf = Leaf(char, weight)
      val bits = List.fill(3)(true)
      decode(leaf)(bits) == List.fill(3)(char)
  }

  property("decode bits given fork node") = forAll(Gen.pick(3, 'a' to 'z')) {
    chars => {
      val leaf1 = Leaf(chars.head, 100) // 1
      val leaf2 = Leaf(chars(1), 10) // 0, 1
      val leaf3 = Leaf(chars.last, 1) // 0, 0
      val fork = Fork(leaf1, Fork(leaf2, leaf3))
      val bits = List(true, false, true, false, false)
      decode(fork)(bits) == chars.toList
    }
  }
}
