#!/bin/sh
cd "$(dirname "$0")" || exit 2
exec java -Dfile.encoding=UTF-8 -jar dist/deep-sea-growth-ai.jar
