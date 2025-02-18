# Pico

Lossless data compression library built in Scala that leverages Cats Effect and FS2 libraries.

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
  * **Compress** with `sbt "run compress -f samples/sample_1kb.txt"`
  * **Decompress** with `sbt "run decompress -f samples/sample_1kb.txt.pico"`

### Logic

#### Compression

1. Stream file data into fixed sized chunks
2. For each chunk, build huffman tree and encode both tree and data
3. Stream output into target file wrapping it with chunk delimiter

### Decompression

1. Stream compressed file data, splitting it by chunk delimiter
2. For each chunk, decode huffman tree and with it, decode encoded data
3. Stream output into target file

### ToDo

- [ ] Introduce parallel processing with `.parEvalMap`
- [ ] Replace fixed chunk size with dynamic chunk evaluation
- [ ] Improve logging and metrics
- [ ] Fix open source code "ToDo's"
- [ ] Release Scala native version

### Licensing

See [LICENSE](LICENSE)
