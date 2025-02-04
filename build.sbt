val scala3Version = "3.6.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "pico2",
    organization := "com.necosta",
    scalaVersion := scala3Version,
  )
