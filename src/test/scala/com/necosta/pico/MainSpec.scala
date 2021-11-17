package com.necosta.pico

import cats.effect.testing.specs2.CatsEffect
import org.specs2.mutable.Specification

import java.nio.file.Files

class MainSpec extends Specification with CatsEffect {
  import Main._

  "Main" should {
    "fail if no file provided" in {
      run(List[String]()).attempt
        .map(x => x.isLeft must beTrue)
    }

    "fail if file does not exist" in {
      val randFileName = new scala.util.Random(47).nextString(10)
      run(List[String](randFileName)).attempt
        .map(x => x.isLeft must beTrue)
    }

    "succeed if file exists" in {
      val randFileName = new scala.util.Random(47).nextString(10)
      // ToDo: Check how to avoid this side effect on tests
      val testFile = Files.createTempFile(randFileName, ".tmp")
      run(List[String](testFile.toFile.getPath)).attempt.map(x => x.isRight must beTrue)
    }
  }
}
