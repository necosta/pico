import sbt.*

object Dependencies {

  private val CatsVersion      = "3.5.7"
  private val ScalatestVersion = "3.2.19"

  lazy val libs: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-effect" % CatsVersion
  )

  lazy val testLibs: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % ScalatestVersion % Test
  )
}
