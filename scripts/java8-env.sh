#!/bin/sh

is_java8_home() {
    [ -n "$1" ] && [ -x "$1/bin/java" ] && "$1/bin/java" -version 2>&1 | sed -n '1p' | grep -Eq 'version "1\.8\.'
}

java8_home=${JAVA_HOME:-}

if ! is_java8_home "$java8_home"; then
    if [ -x /usr/libexec/java_home ]; then
        java8_home=$(/usr/libexec/java_home -v 1.8 2>/dev/null || true)
    fi
fi

if ! is_java8_home "$java8_home"; then
    echo "错误：未找到 JDK 8。" >&2
    echo "请安装 JDK 8，或在运行前将 JAVA_HOME 设为 JDK 8 的安装目录。" >&2
    return 1 2>/dev/null || exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
    echo "错误：未找到 Maven，请安装 Maven 3.6.3 或更高的 3.x 版本。" >&2
    return 1 2>/dev/null || exit 1
fi

export JAVA_HOME="$java8_home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "使用 Java：$($JAVA_HOME/bin/java -version 2>&1 | sed -n '1p')"
echo "使用 Maven：$(mvn -version 2>&1 | sed -n '1p')"
