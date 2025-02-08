# Pico (v2)

Lossless data compression/decompression app using Cats Effect library.

## Pre-requisites

* Download [SBT](https://www.scala-sbt.org/download/)
* Download [Java](https://openjdk.org/projects/jdk/23/)

### How-to

* Format code -> `sbt formatAll` (see [alias.sbt](alias.sbt))
* Build/Compile -> `sbt compile`
* Run unit tests -> `sbt test`
* Run unit tests with code coverage report -> `sbt unitTests` (see [alias.sbt](alias.sbt))
  * Check report on [index.html](target/scala-3.6.3/scoverage-report/index.html)
* Run program:
  * **Compress** with `sbt "run compress -f source.txt"`
  * **Decompress** with `sbt "run decompress -f source.txt.pico"`

### Licensing

See [LICENSE](LICENSE)
