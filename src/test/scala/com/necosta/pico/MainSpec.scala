package com.necosta.pico

import cats.effect.ExitCode
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.testing.scalatest.AsyncIOSpec

class MainSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {
  "Main" should "fail when no arguments" in {
    val res = Main.run(List.empty[String])
    res.asserting(_ shouldBe ExitCode.Error)
  }

  "Main" should "fail with invalid command" in {
    val res = Main.run(List[String]("invalid"))
    res.asserting(_ shouldBe ExitCode.Error)
  }

  "Main" should "fail with invalid command arguments" in {
    val res = Main.run(List[String]("compress", "-f"))
    res.asserting(_ shouldBe ExitCode.Error)
  }

  "Main" should "fail with invalid command file" in {
    val res = Main.run(List[String]("compress", "-f", "invalid.txt"))
    res.asserting(_ shouldBe ExitCode.Error)
  }

  "Main" should "succeed with valid compress command arguments" in {
    val res = Main.run(List[String]("compress", "-f", "source.txt"))
    res.asserting(_ shouldBe ExitCode.Success)
  }

  "Main" should "succeed with valid decompress command  arguments" in {
    val res = Main.run(List[String]("decompress", "-f", "source.txt.pico"))
    res.asserting(_ shouldBe ExitCode.Success)
  }
}
