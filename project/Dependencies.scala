import sbt.*

object Dependencies {

  private val CatsVersion      = "3.5.7"
  private val Fs2Version       = "3.11.0"
  private val ScalatestVersion = "3.2.19"

  lazy val libs: Seq[ModuleID] = Seq(
    "co.fs2"        %% "fs2-io"      % Fs2Version,
    "org.typelevel" %% "cats-effect" % CatsVersion
  )

  lazy val testLibs: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % ScalatestVersion % Test
  )
}
