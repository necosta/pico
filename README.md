# Pico - Compression

Lossless data compression library using Cats Effect library.

Example: `sbt run file.txt` shall create a file with name `file.txt.pico` and file size smaller than source file.

## Algorithms supported

* [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding)

## Prerequisites

* Install [SBT](https://www.scala-sbt.org/download.html)

### How-to

* Build/Compile (with automatic code formatting): `sbt compile`
* Unit test: `sbt test`
* Integration test: ~~sbt it:test~~ (TBD)
* Analyse test code coverage: `sbt coverage test coverageReport`
* Run: `sbt run sourceFileName`

### License

See [LICENSE](LICENSE)
