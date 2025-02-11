package com.necosta.pico.file

import FileCodec.TreeSeparator
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.flatspec.{AnyFlatSpec, AsyncFlatSpec}
import org.scalatest.matchers.should.Matchers

class FileCodecSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {
  "FileCodec" should "encode/decode simple tree and data" in {
    val input        = "aaaaaa".toList.map(_.toByte)
    val treeExpected = List[Byte]('L'.toByte, 'a'.toByte, TreeSeparator.toByte)
    val dataExpected = List[Byte](-65, 2)
    val expected     = treeExpected ++ dataExpected
    for {
      encoded <- FileCodec[IO].encode(input)
    } yield encoded match {
      case Right(v)  => v shouldBe expected
      case Left(str) => fail(str)
    }
    for {
      decoded <- FileCodec[IO].decode(expected)
    } yield decoded match {
      case Right(v)  => v shouldBe input
      case Left(str) => fail(str)
    }
  }

  "FileCodec" should "encode/decode complex tree and data" in {
    val input = "abbcdee".toList.map(_.toByte)
    val f     = 'F'.toByte
    val l     = 'L'.toByte
    val c     = ','.toByte
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
    for {
      encoded <- FileCodec[IO].encode(input)
    } yield encoded match {
      case Right(v)  => v shouldBe expected
      case Left(str) => fail(str)
    }
    for {
      decoded <- FileCodec[IO].decode(expected)
    } yield decoded match {
      case Right(v)  => v shouldBe input
      case Left(str) => fail(str)
    }
  }
}
