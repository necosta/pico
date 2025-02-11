import sbt.*

object Dependencies {

  private val CatsVersion     = "3.5.7"
  private val ClippVersion    = "0.6.8"
  private val Fs2Version      = "3.11.0"
  private val Log4CatsVersion = "2.7.0"
  private val LogbackVersion  = "1.5.16"

  private val CatsEffectTestingVersion = "1.6.0"
  private val ScalatestVersion         = "3.2.19"

  lazy val libs: Seq[ModuleID] = Seq(
    "co.fs2"          %% "fs2-io"             % Fs2Version,
    "io.github.vigoo" %% "clipp-core"         % ClippVersion,
    "io.github.vigoo" %% "clipp-cats-effect3" % ClippVersion,
    "org.typelevel"   %% "cats-effect"        % CatsVersion,
    "org.typelevel"   %% "log4cats-core"      % Log4CatsVersion,
    "org.typelevel"   %% "log4cats-noop"      % Log4CatsVersion,
    "org.typelevel"   %% "log4cats-slf4j"     % Log4CatsVersion,
    "ch.qos.logback"   % "logback-classic"    % LogbackVersion
  )

  lazy val testLibs: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest"                     % ScalatestVersion         % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % CatsEffectTestingVersion % Test
  )
}
