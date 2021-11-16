import sbt._

object Dependencies {

  private val CatsVersion = "3.2.9"
  private val ScalaCheckVersion = "1.15.4"

  val libs = Seq(
    "org.typelevel" %% "cats-effect" % CatsVersion
  )

  val testLibs = Seq(
    "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test
  )
}
