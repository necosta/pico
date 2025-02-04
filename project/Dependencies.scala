import sbt.*

object Dependencies {

  private val CatsVersion = "3.5.7"

  lazy val libs: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-effect" % CatsVersion
  )
}

