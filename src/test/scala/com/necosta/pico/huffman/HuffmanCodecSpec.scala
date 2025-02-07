package com.necosta.pico.huffman

import cats.syntax.all.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HuffmanCodecSpec extends AnyFlatSpec with Matchers {
  "HuffmanCodec" should "encode/decode simple tree" in {
    val inputBytes = "abbbc".toList.map(_.toByte)
    val inputTree  = HuffmanTree.create(inputBytes)
    // a -> true, true ; b -> false ; c -> true, false
    val expected     = List(true, true, false, false, false, true, false)
    val encodeResult = HuffmanCodec.encode(inputTree)(inputBytes)
    encodeResult shouldBe expected.valid
    val decodeResult = HuffmanCodec.decode(inputTree)(expected)
    decodeResult shouldBe inputBytes.valid
  }

  "HuffmanCodec" should "encode/decode complex tree" in {
    val inputBytes = "dddddabcxwz".toList.map(_.toByte)
    val inputTree  = HuffmanTree.create(inputBytes)
    /* Fork(
       Leaf(d,Some(5)),
       Fork(
         Fork(
           Leaf(x,Some(1)),
           Leaf(z,Some(1))
         ),
         Fork(
           Fork(
             Leaf(a,Some(1)),
             Leaf(b,Some(1))),
           Fork(
             Leaf(c,Some(1)),
             Leaf(w,Some(1))
           )
         )
       )
    )
    d -> List(true)
    a -> List(false, false, true, true)
    b -> List(false, false, true, false)
    c -> List(false, false, false, true)
    x -> List(false, false, false, false)
    w -> List(false, true, true)
    z -> List(false, true, false)
     */
    val expected = List(true, true, true, true, true, false, false, true, true, false, false, true, false, false, false,
      false, true, false, false, false, false, false, true, true, false, true, false)
    val encodeResult = HuffmanCodec.encode(inputTree)(inputBytes)
    encodeResult shouldBe expected.valid
    val decodeResult = HuffmanCodec.decode(inputTree)(expected)
    decodeResult shouldBe inputBytes.valid
  }
}
