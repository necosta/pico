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
    val res = Main.run(List[String]("compress", "-f", "samples/sample_1kb.txt"))
    res.asserting(_ shouldBe ExitCode.Success)
  }

  "Main" should "succeed with valid decompress command arguments" in {
    val res = Main.run(List[String]("decompress", "-f", "samples/sample_1kb.txt.pico"))
    res.asserting(_ shouldBe ExitCode.Success)
  }

  "Main" should "fail with a negative chunk size value" in {
    val res = Main.run(List[String]("compress", "-f", "samples/sample_1kb.txt", "-c", "-10"))
    res.asserting(_ shouldBe ExitCode.Error)
  }

  "Main" should "fail with a chunk size value set against decompress command" in {
    val res = Main.run(List[String]("decompress", "-f", "samples/sample_1kb.txt.pico", "-c", "10"))
    res.asserting(_ shouldBe ExitCode.Error)
  }
}
