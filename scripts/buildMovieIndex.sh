#!/bin/env bash

JAR="beta-rec-jar-with-dependencies.jar"

JVM_ARGS="-Xmx 4G -Xms 4G"

cmd="java -cp ${JAR} com.betarec.data.SqlHelper \
    ${JVM_ARGS} "


java -cp "${JAR}" com.betarec.index.builder.MovieIndexBuilder
