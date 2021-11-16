import Dependencies._

val Scala2 = "2.13.6"

val KindProjectorVersion = "0.13.2"
val BetterMonadicForVersion = "0.3.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.necosta",
    name := "pico",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := Scala2,
    libraryDependencies ++= libs ++ testLibs,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % KindProjectorVersion cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % BetterMonadicForVersion)
  )
