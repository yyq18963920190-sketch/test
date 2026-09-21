#!/bin/sh
cd "$(dirname "$0")" || exit 2
export PYTHONUTF8=1
exec python3 run_tests.py "$@"
