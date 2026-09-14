# AB 测试验证和运行说明

本项目包含 6 个 `handoff` 测试类，共 36 个独立测试方法：A 组 18 条，B 组 18 条。
测试代码由往届工程的测试结构与当前测试用例清单适配而来，测试数据以当前工程中的 Java 用例和排行榜夹具为准。

## 测试范围

| 测试类 | 用例编号 | 数量 | 覆盖内容 |
| --- | --- | ---: | --- |
| `handoff.a.AMovementTest` | DSG-A-001—008 | 8 | 玩家鱼初始化、移动与边界 |
| `handoff.a.AUpgradeTest` | DSG-A-009—014 | 6 | 分数升级边界 |
| `handoff.a.AWinTest` | DSG-A-015—018 | 4 | 胜利状态与排行榜 |
| `handoff.b.BFishCollisionTest` | DSG-B-001—008 | 8 | 鱼碰撞与等级关系 |
| `handoff.b.BDaoJuTest` | DSG-B-009—014 | 6 | 道具及相关边界场景 |
| `handoff.b.BTimeTest` | DSG-B-015—018 | 4 | 时间设置与显示边界 |

旧工程的 `AllTests`、Excel 导入器、Mockito、Jupiter、图像比较和线程测试不在本次运行范围内。
Maven Surefire 会直接发现以上 6 个测试类，不需要额外维护测试套件。

## 环境要求

- JDK 8。
- Maven 3.6.3 及以上、4.0 以下。
- 从项目根目录执行命令。
- 同一个工程目录一次只运行一个 Maven 测试进程。

项目使用了已从新版本 JDK 移除的 `Thread.resume()` 和 `Thread.suspend()`，因此仅配置 Java 8 编译级别不能替代 JDK 8 运行环境。
运行前先确认版本：

```sh
java -version
mvn -version
```

如果系统安装了多个 JDK，可先将 `JAVA_HOME` 指向 JDK 8：

```sh
export JAVA_HOME="/path/to/jdk8/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

## 运行命令

全量执行：

```sh
mvn -B -ntp clean test
```

按模块执行：

```sh
mvn -B -ntp -Dtest=handoff.a.AMovementTest test
mvn -B -ntp -Dtest=handoff.a.AUpgradeTest test
mvn -B -ntp -Dtest=handoff.a.AWinTest test
mvn -B -ntp -Dtest=handoff.b.BFishCollisionTest test
mvn -B -ntp -Dtest=handoff.b.BDaoJuTest test
mvn -B -ntp -Dtest=handoff.b.BTimeTest test
```

选择性执行只能说明对应模块的结果，不能作为 36 条用例全部完成的证据。

## 隔离机制

Surefire 以单进程、非并行方式在 `target/ab-test-work` 中运行测试。
构建过程会把游戏图片和业务排行榜复制到该目录，使业务代码中的相对路径指向测试副本。
涉及排行榜的用例会在执行前写入指定夹具，并在结束后恢复测试副本的原始字节。

测试使用 Swing 事件派发线程和 headless 模式，不创建 `GameFrame`，也不启动 `ActListener` 线程。
这套测试用于验证所列业务方法和组件属性，不代表完整游戏窗口、动画及线程行为已通过人工验收。

## 结果检查

全量执行应发现 36 条用例，且不应出现跳过项或旧套件的重复用例。
测试报告位于：

```text
target/surefire-reports/
```

检查 XML 报告中的 `tests`、`failures`、`errors` 和 `skipped`，并逐项核对 DSG 用例编号。
环境或编译失败、测试执行错误和断言失败是三类不同结果，应分别记录。

A-017、A-018、B-009、B-018 是当前重点核验的候选缺陷用例。出现断言失败时应保留日志和实际值，不得通过忽略用例、忽略失败或修改正确预期来隐藏结果。A-017 的排行榜空位规则及 B-018 的秒数规范化责任仍需结合需求确认。

每次正式执行应记录日期、操作系统、JDK、Maven、代码提交号、运行命令、执行数量、失败详情及报告路径。缺陷修复后使用相同输入复测，并同步更新用例清单和缺陷记录。

## 本次验证结果

2026-09-14 在干净的 `main` 基线 `08847a5` 上，使用 Azul Zulu JDK 1.8.0_502 和 Maven 3.9.9 执行：

```sh
mvn -B -ntp clean test
```

实际发现并执行 36 条用例：32 条通过、4 条失败、0 错误、0 跳过。
失败用例为 A-017、A-018、B-009、B-018，与当前重点核验的候选缺陷一致。
Maven 因断言失败返回非零状态；环境检查、源码编译和测试发现过程均正常。

## 版本管理

- A 组维护 `pom.xml`、`handoff/support`、`handoff/a`、排行榜夹具及本文档。
- B 组维护 `handoff/b`。
- 测试代码、数据、配置和关键 AI 对话记录须纳入 Git。
- `target`、IDE 缓存及本地运行日志不提交到仓库。
