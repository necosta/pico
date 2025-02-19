package com.necosta.pico.benchmark

import cats.effect.IO
import com.necosta.pico.file.FileCodec
import org.openjdk.jmh.annotations.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import cats.effect.unsafe.implicits.global

import java.util.concurrent.TimeUnit
import scala.util.Random

@BenchmarkMode(Array(Mode.AverageTime)) // Measures avg time per operation
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Outputs in milliseconds
@State(Scope.Thread) // Each thread gets a separate instance
class FileCodecBenchmark {

  private def getRandomByte = Random.nextInt(256).toByte

  private val input: List[Byte] = List.fill(1000)(getRandomByte)

  @Benchmark
  def benchmarkEncode(): Either[String, List[Byte]] = {
    implicit val logger: Logger[IO] = NoOpLogger.impl
    FileCodec[IO].encode(input).unsafeRunSync()
  }

  @Benchmark
  def benchmarkDecode(): Either[String, List[Byte]] = {
    implicit val logger: Logger[IO] = NoOpLogger.impl
    FileCodec[IO].decode(input).unsafeRunSync()
  }
}
