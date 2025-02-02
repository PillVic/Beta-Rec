#!/bin/env bash

#x: print the command before excute it
#e: once a command fail (return no zero)
#u: when using undefined variable, exit
#o pipefail: once a single pipe fail exit

set -xeuo pipefail

JAR="beta-rec-jar-with-dependencies.jar"

JVM_ARGS="-XX:+UseZGC"

java "${JVM_ARGS}" \
    -cp "${JAR}" com.betarec.recommend.RecommendMain \
    -userId 401 \
    -movie_index_path "./index/movie" \
    -user_index_path "./index/user" \
    -limit 20
