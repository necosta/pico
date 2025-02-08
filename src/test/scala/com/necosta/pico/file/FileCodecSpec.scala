package com.necosta.pico.file

import FileCodec.{TreeSeparator, decode}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FileCodecSpec extends AnyFlatSpec with Matchers {
  "FileCodec" should "encode/decode simple tree and data" in {
    val input        = "aaaaaa".toList.map(_.toByte)
    val encoded      = FileCodec.encode(input)
    val treeExpected = List[Byte]('L'.toByte, 'a'.toByte, TreeSeparator.toByte)
    val dataExpected = List[Byte](-65, 2)
    val expected     = treeExpected ++ dataExpected
    encoded match {
      case Right(v)  => v shouldBe expected
      case Left(str) => fail(str)
    }
    val decoded = FileCodec.decode(expected)
    decoded match {
      case Right(v)  => v shouldBe input
      case Left(str) => fail(str)
    }
  }

  "FileCodec" should "encode/decode complex tree and data" in {
    val input   = "abbcdee".toList.map(_.toByte)
    val encoded = FileCodec.encode(input)
    val f       = 'F'.toByte
    val l       = 'L'.toByte
    val c       = ','.toByte
    val treeExpected = List[Byte](
      f,
      f,
      l,
      'd'.toByte,
      c,
      l,
      'b'.toByte,
      c,
      f,
      l,
      'e'.toByte,
      c,
      f,
      l,
      'a'.toByte,
      c,
      l,
      'c'.toByte,
      TreeSeparator.toByte
    )
    val dataExpected = List[Byte](-76, -75, 0)
    val expected     = treeExpected ++ dataExpected
    encoded match {
      case Right(v)  => v shouldBe expected
      case Left(str) => fail(str)
    }
    val decoded = FileCodec.decode(expected)
    decoded match {
      case Right(v)  => v shouldBe input
      case Left(str) => fail(str)
    }
  }
}
