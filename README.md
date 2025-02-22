# Pico

Lossless data compression library built in Scala that leverages Cats Effect and FS2 libraries.

## Pre-requisites

* Download [SBT](https://www.scala-sbt.org/download/)
* Download [Java](https://openjdk.org/projects/jdk/23/)

## How-to

* Format code -> `sbt formatAll` (see [alias.sbt](alias.sbt))
* Build/Compile -> `sbt compile`
* Run unit tests -> `sbt test`
* Run unit tests with code coverage report -> `sbt unitTests` (see [alias.sbt](alias.sbt))
  * Check report on [index.html](target/scala-3.6.3/scoverage-report/index.html)
* Run program:
  * **Compress mode** -> `sbt "run compress -f samples/sample_1kb.txt"`
  * **Decompress mode** -> `sbt "run decompress -f samples/sample_1kb.txt.pico"`

#### Benchmarking and Profiling

* Run Async profiler -> `./asprof -e cpu -d 30 -f profiler.html (pid)`
  * Get pid by running `jps -l | grep -i pico`
  * Also options: `-e alloc` (memory), `-e lock` (lock contention), `-e wall` (wall clock time)
* Run JMH benchmarks -> `sbt clean Jmh/run`
  * `sbt clean Jmh/run -i 3 -wi 3 -f 1 -t 2` -> (3 iterations, 3 warmup iterations, 1 fork, 2 threads)

## CLI parameters

* **Command:** `compress|decompress`
  * Selects compress or decompress mode
* **File:** `-f fileName` or `--file fileName`
  * Target file to compress or decompress
* **ChunkSize:** `-c chunkSize` or `--chunkSize chunkSize` 
  * Only applies to `compress` command mode, source file stream chunk size in kilobytes units
* **Verbosity:** `-d` or `--debug` (for debug mode) and `t` or `--trace` (for trace mode)
  * Updates root log level for a more verbose logging

## Logic

### Compression

1. Stream file data into fixed sized chunks
2. For each chunk, build huffman tree and encode both tree and data
3. Stream output into target file wrapping it with chunk delimiter

### Decompression

1. Stream compressed file data, splitting it by chunk delimiter
2. For each chunk, decode huffman tree and with it, decode encoded data
3. Stream output into target file

## Nice To Have

- [ ] Release as Scala native

## Licensing

See [LICENSE](LICENSE)
