addCommandAlias("formatAll", "scalafmtAll ; scalafmtSbt")
// See https://github.com/scoverage/sbt-scoverage/issues/522 for why we need clean and reboot
addCommandAlias("unitTests", "reboot ; clean ; coverageOn ; test ; coverageReport ; coverageOff")
