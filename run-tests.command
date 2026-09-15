#!/bin/sh

set -u

project_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$project_dir" || exit 1

. "$project_dir/scripts/java8-env.sh" || exit 1

echo "正在执行 36 条全量测试……"
mvn -B -ntp clean test
test_status=$?

echo
echo "测试报告：$project_dir/target/surefire-reports/"
if [ "$test_status" -ne 0 ]; then
    echo "测试命令返回非零状态。请查看上方失败用例；当前已知缺陷也会导致 BUILD FAILURE。" >&2
fi

exit "$test_status"
