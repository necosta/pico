package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HuffmanSerdeSpec extends AnyFlatSpec with Matchers {
  "HuffmanSerde" should "serialise/deserialise simple tree" in {
    // Ignore weights for serde
    val input    = Fork(Leaf('a', None), Fork(Leaf('c', None), Leaf('b', None)))
    val expected = "FLa,FLc,Lb"
    HuffmanSerde.serialise(input) shouldBe expected
    HuffmanSerde.deserialise(expected) shouldBe input
  }

  "HuffmanSerde" should "serialise/deserialise simple tree with comma char" in {
    // Ignore weights for serde
    val input    = Fork(Leaf('a', None), Fork(Leaf(',', None), Leaf('b', None)))
    val expected = "FLa,FL,,,Lb"
    HuffmanSerde.serialise(input) shouldBe expected
    HuffmanSerde.deserialise(expected) shouldBe input
  }

  "HuffmanSerde" should "serialise/deserialise complex tree" in {
    // Ignore weights for serde
    val input = Fork(
      Fork(Leaf('e', None), Leaf('f', None)),
      Fork(
        Fork(
          Fork(Leaf('a', None), Fork(Leaf('c', None), Leaf('b', None))),
          Fork(Leaf('z', None), Fork(Leaf('w', None), Leaf('x', None)))
        ),
        Leaf('d', None)
      )
    )
    val expected = "FFLe,Lf,FFFLa,FLc,Lb,FLz,FLw,Lx,Ld"
    HuffmanSerde.serialise(input) shouldBe expected
    HuffmanSerde.deserialise(expected) shouldBe input
  }
}
