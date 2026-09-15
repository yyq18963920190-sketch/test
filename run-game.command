#!/bin/sh

set -u

project_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$project_dir" || exit 1

. "$project_dir/scripts/java8-env.sh" || exit 1

echo "正在编译游戏……"
mvn -B -ntp -DskipTests compile || exit $?

echo "正在启动深海成长……"
exec "$JAVA_HOME/bin/java" -cp "$project_dir/target/classes" fish.GameFrame
