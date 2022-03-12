#!/bin/env bash

#x: print the command before excute it
#e: once a command fail (return no zero)
#u: when using undefined variable, exit
#o pipefail: once a single pipe fail exit

#set -xeuo pipefail


USER="rec"
PASSWORD="123456"

mysql -u"${USER}" -p"${PASSWORD}" < init.sql

#依照sample表进行批量建表

for suffix in {0..99}
do
    SplitTables=("genome_scores" "ratings")
    for table in "${SplitTables[@]}"
    do
        SQL="Use MovieLens;CREATE TABLE ${table}_${suffix} LIKE ${table}_sample;"
        echo "${SQL}"
        mysql --user="${USER}" --password="${PASSWORD}" --execute="${SQL}"
    done
done
