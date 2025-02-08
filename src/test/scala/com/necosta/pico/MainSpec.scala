package com.necosta.pico

import cats.effect.ExitCode
import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFlatSpec with Matchers {
  // ToDo: Handle exception (return success with ExitCode.Error)
  "Main" should "fail when no arguments" in {
    Main
      .run(List.empty[String])
      .attempt
      .map(_.isLeft shouldBe true)
  }

  "Main" should "fail with invalid command" in {
    val res = Main
      .run(List[String]("invalid"))
      .unsafeRunSync()
    res shouldBe ExitCode.Error
  }

  "Main" should "fail with invalid command arguments" in {
    val res = Main
      .run(List[String]("compress", "-f"))
      .unsafeRunSync()
    res shouldBe ExitCode.Error
  }

  // ToDo: Handle invalid file exception
  "Main" should "fail with invalid command file" in {
    Main
      .run(List[String]("compress", "-f", "invalid.txt"))
      .attempt
      .map(_.isLeft shouldBe true)
  }

  "Main" should "succeed with valid arguments" in {
    val res = Main
      .run(List[String]("compress", "-f", "source.txt"))
      .unsafeRunSync()
    res shouldBe ExitCode.Success
  }
}
