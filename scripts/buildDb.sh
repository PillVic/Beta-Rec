#!/bin/env bash

#x: print the command before excute it
#e: once a command fail (return no zero)
#u: when using undefined variable, exit
#o pipefail: once a single pipe fail exit

#set -xeuo pipefail


USER="rec"
PASSWORD="123456"

mysql -u"${USER}" -p"${PASSWORD}" < init.sql
