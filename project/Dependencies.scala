import sbt._

object Dependencies {

  private val CatsVersion       = "3.3.4"
  private val ClippVersion      = "0.6.4"
  private val ScalaCheckVersion = "1.15.4"
  private val CatsSpecs2Version = "1.3.0"

  val libs = Seq(
    "org.typelevel"   %% "cats-effect"        % CatsVersion,
    "io.github.vigoo" %% "clipp-core"         % ClippVersion,
    "io.github.vigoo" %% "clipp-cats-effect3" % ClippVersion
  )

  val testLibs = Seq(
    "org.typelevel"  %% "cats-effect-testing-specs2" % CatsSpecs2Version % Test,
    "org.scalacheck" %% "scalacheck"                 % ScalaCheckVersion % Test
  )
}
