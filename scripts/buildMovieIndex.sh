#!/bin/env bash

#x: print the command before excute it
#e: once a command fail (return no zero)
#u: when using undefined variable, exit
#o pipefail: once a single pipe fail exit

set -xeuo pipefail

JAR="beta-rec-jar-with-dependencies.jar"


java -XX:+UseZGC -Xms1024M -Xms1024M \
    -cp "${JAR}" com.betarec.index.builder.MovieIndexBuilder \
    -threads 5 \
    -index "./index/movie" \
    -batchSize 400 \
