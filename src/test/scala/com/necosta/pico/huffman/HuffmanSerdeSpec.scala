package com.necosta.pico.huffman

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.necosta.pico.huffman.Huffman.*
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

class HuffmanSerdeSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {
  implicit val logger: Logger[IO] = NoOpLogger[IO]

  "HuffmanSerde" should "serialise/deserialise simple tree" in {
    // Ignore weights for serde
    val input    = Fork(Leaf('a', None), Fork(Leaf('c', None), Leaf('b', None)))
    val expected = "FLa,FLc,Lb"
    HuffmanSerde[IO].serialise(input).asserting(_ shouldBe expected) *>
      HuffmanSerde[IO].deserialise(expected).asserting(_ shouldBe input)
  }

  "HuffmanSerde" should "serialise/deserialise simple tree with comma char" in {
    // Ignore weights for serde
    val input    = Fork(Leaf('a', None), Fork(Leaf(',', None), Leaf('b', None)))
    val expected = "FLa,FL,,,Lb"
    HuffmanSerde[IO].serialise(input).asserting(_ shouldBe expected) *>
      HuffmanSerde[IO].deserialise(expected).asserting(_ shouldBe input)
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
    HuffmanSerde[IO].serialise(input).asserting(_ shouldBe expected) *>
      HuffmanSerde[IO].deserialise(expected).asserting(_ shouldBe input)
  }
}
