import sbt._

object Dependencies {

  private val CatsVersion = "3.2.9"
  private val ScalaCheckVersion = "1.15.4"
  private val CatsSpecs2Version = "1.3.0"

  val libs = Seq(
    "org.typelevel" %% "cats-effect" % CatsVersion
  )

  val testLibs = Seq(
    "org.typelevel" %% "cats-effect-testing-specs2" % CatsSpecs2Version % Test,
    "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test
  )
}
