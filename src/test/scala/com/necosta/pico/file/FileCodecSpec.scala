package com.necosta.pico.file

import FileCodec.TreeSeparator
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

class FileCodecSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {
  implicit val logger: Logger[IO] = NoOpLogger[IO]

  "FileCodec" should "encode/decode simple tree and data" in {
    val input        = "aaaaaa".toList.map(_.toByte)
    val treeExpected = List[Byte]('L'.toByte, 'a'.toByte, TreeSeparator.toByte)
    val dataExpected = List[Byte](-65, 2)
    val expected     = treeExpected ++ dataExpected
    for {
      encoded <- FileCodec[IO].encode(input)
      _ = encoded match {
        case Right(v)  => v shouldBe expected
        case Left(str) => fail(str)
      }
      decoded <- FileCodec[IO].decode(expected)
      _ = decoded match {
        case Right(v)  => v shouldBe input
        case Left(str) => fail(str)
      }
    } yield ()
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
      'e'.toByte,
      c,
      f,
      l,
      'b'.toByte,
      c,
      f,
      l,
      'a'.toByte,
      c,
      l,
      'c'.toByte,
      TreeSeparator.toByte
    )
    val dataExpected = List[Byte](-86, -70, 0)
    val expected     = treeExpected ++ dataExpected
    for {
      encoded <- FileCodec[IO].encode(input)
      _ = encoded match {
        case Right(v)  => v shouldBe expected
        case Left(str) => fail(str)
      }
      decoded <- FileCodec[IO].decode(expected)
      _ = decoded match {
        case Right(v)  => v shouldBe input
        case Left(str) => fail(str)
      }
    } yield ()
  }
}
