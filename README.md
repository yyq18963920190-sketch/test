# 深海成长（Deep Sea Growth）

这是一个基于 Java Swing 的“大鱼吃小鱼”课程项目。玩家控制鱼类移动，通过碰撞、得分和升级完成游戏。仓库同时包含 JUnit 4 自动化测试，覆盖玩家鱼移动、升级、胜利、碰撞、道具和时间逻辑。

## 项目结构

```text
deep-sea-growth/
├── README.md                         # 项目、环境与运行说明
├── pom.xml                          # Maven 依赖、JDK 约束和测试配置
├── run-game.command                 # macOS 双击启动游戏
├── run-tests.command                # macOS 双击执行全量测试
├── scripts/
│   └── java8-env.sh                 # 脚本共用的 JDK 8 检查与配置
├── docs/
│   ├── AB_TESTING.md               # AB 测试范围与历史验证记录
│   └── A-01实施记录.md
├── src/main/java/fish/
│   ├── GameFrame.java              # Swing 主窗口和程序入口
│   ├── FishUI.java                 # 游戏画面、碰撞、升级和排行榜逻辑
│   ├── ActListener.java            # 按钮、键盘和游戏线程控制
│   ├── Fish.java                   # 鱼类状态与移动模型
│   ├── DaoJu.java                  # 道具模型
│   └── ImagePool.java              # 图片资源加载
├── src/main/resources/
│   ├── resource/                   # 背景、鱼、道具和排名图片
│   ├── record/排行榜.txt          # 排行榜数据
│   └── record0.txt...record2.txt   # 三个游戏存档
├── src/test/java/handoff/
│   ├── a/                          # 移动、升级、胜利与排行榜测试
│   ├── b/                          # 碰撞、道具与时间测试
│   └── support/                    # Swing 执行和资源隔离工具
├── src/test/resources/ab/ranking/      # 排行榜测试夹具
└── target/                              # Maven 生成物，不提交 Git
```

## 环境要求

- macOS（一键 `.command` 脚本适用的系统）。
- JDK 8；项目必须由 JDK 8 运行，仅设置 Java 8 编译级别不够。
- Maven 3.6.3 及以上、4.0 以下。

`ActListener` 使用了新版 JDK 已移除的 `Thread.suspend()` 和 `Thread.resume()`，`pom.xml` 因此会主动拒绝 JDK 9 及以上环境。

先检查本机环境：

```sh
java -version
mvn -version
/usr/libexec/java_home -V
```

macOS 安装了 JDK 8 后，可在当前终端配置：

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
export PATH="$JAVA_HOME/bin:$PATH"
```

再次执行 `java -version` 和 `mvn -version`，两者都应显示 Java 1.8。本项目的一键脚本会优先使用当前 `JAVA_HOME`；如果它不是 JDK 8，则会通过 macOS `java_home` 自动查找 JDK 8。

## 一键启动游戏

在 Finder 中双击 `run-game.command`。脚本会检查 JDK 8 和 Maven，编译主代码，然后启动 `fish.GameFrame`。游戏运行期间不要关闭脚本打开的终端窗口。

也可在项目根目录中执行：

```sh
./run-game.command
```

脚本等价于先编译，再从项目根目录启动：

```sh
mvn -B -ntp -DskipTests compile
java -cp target/classes fish.GameFrame
```

不要在其他工作目录直接运行主类，因为当前游戏代码使用 `src/main/resources/...` 相对路径读写图片、排行榜和存档。

### IntelliJ IDEA

1. 将 Project SDK 和 Maven Runner JRE 都设为 JDK 8。
2. 以 Maven 项目导入 `pom.xml`。
3. 运行 `src/main/java/fish/GameFrame.java` 中的 `main` 方法。
4. 将 Working directory 设为项目根目录 `deep-sea-growth`。

## 一键运行测试

在 Finder 中双击 `run-tests.command`，或在项目根目录执行：

```sh
./run-tests.command
```

一键脚本执行的全量命令是：

```sh
mvn -B -ntp clean test
```

可以只运行一个测试类，例如：

```sh
mvn -B -ntp -Dtest=handoff.a.AMovementTest test
mvn -B -ntp -Dtest=handoff.b.BFishCollisionTest test
```

全量测试应发现 6 个测试类、36 条用例，不应出现跳过项。详细报告位于：

```text
target/surefire-reports/
```

Surefire 会在 `target/ab-test-work` 中复制游戏资源并执行测试，排行榜用例也会在测试后恢复隔离副本，以避免修改正式排行榜。同一工程目录一次只应运行一个 Maven 测试进程。

## 当前验证结果

2026-09-15 在 macOS、Azul Zulu JDK 1.8.0_502 和 Maven 3.9.9 下验证：

- 主代码和测试代码均编译成功。
- `fish.GameFrame` 可启动，游戏 GUI 进程与窗口创建成功。
- 全量测试执行 36 条：32 条通过、4 条失败、0 错误、0 跳过。
- 失败用例为 `DSG-A-017`、`DSG-A-018`、`DSG-B-009` 和 `DSG-B-018`。
- 因为测试正常检出了未修复问题，Maven 返回 `BUILD FAILURE`。这不是一键脚本或测试环境故障。

当前已复现问题：

1. 整分钟排行榜记录（如 `1:00`）被错误当成空记录。
2. 更新排行榜时改写了本局时间变量。
3. `ImagePool.getImage(20)` 没有边界保护，会触发数组越界。
4. `10:60` 没有归一化为 `11:00`。

一键测试脚本会保留 Maven 的真实退出状态，不会忽略失败。详细的 AB 测试范围和结果判读见 [`docs/AB_TESTING.md`](docs/AB_TESTING.md)。
