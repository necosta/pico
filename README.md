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

### CLI parameters

* **Command:** `compress|decompress`
  * Selects compress or decompress mode
* **File:** `-f fileName` or `--file fileName`
  * Target file to compress or decompress
* **ChunkSize:** `-c chunkSize` or `--chunkSize chunkSize` 
  * Only applies to `compress` command mode, source file stream chunk size in kilobytes units

### Logic

#### Compression

1. Stream file data into fixed sized chunks
2. For each chunk, build huffman tree and encode both tree and data
3. Stream output into target file wrapping it with chunk delimiter

### Decompression

1. Stream compressed file data, splitting it by chunk delimiter
2. For each chunk, decode huffman tree and with it, decode encoded data
3. Stream output into target file

### Nice To Have

- [ ] Improve debug and trace level logging
- [ ] Add verbose CLI parameter
- [ ] Add time/benchmark metrics
- [ ] Release as Scala native

### Licensing

See [LICENSE](LICENSE)
