# 深海成长记 · 模块一 + AI 模型完整项目

这是在附件二 Java Swing 游戏源码上实际接入 AI 后的完整工程。保留原游戏的图片、存档、排行榜、36条JUnit测试及Maven配置，增加本地kNN模型、Java推理、安全过滤、AI建议/托管界面和集成测试。

**游戏直接在Java进程中加载模型并推理，不需要Python服务、联网、API Key或GPU。** Python只用于训练/导出模型和自动化测试编排。

## 直接玩游戏

已附 `dist/deep-sea-growth-ai.jar`，解压完整工程后：

- Windows：双击 `run-game.bat`。
- macOS/Linux：在项目根目录执行 `sh run-game.command`。
- 终端通用方式：`java -Dfile.encoding=UTF-8 -jar dist/deep-sea-growth-ai.jar`。

游戏运行需要Java 8或更高版本。请保留整个目录并从根目录启动，存档和排行榜仍按原工程写入 `src/main/resources/`。不要只把JAR单独移走。已在本机JDK 21编译并执行测试，生成Java 8目标字节码；没有实测所有Java版本。

操作：点击“开始游戏”，W/A/S/D移动，P暂停。点击右上角AI按钮或按H，在“关闭 → 建议 → 托管 → 关闭”之间切换。

| 模式 | 行为 |
|---|---|
| 关闭 | 完全手动操作 |
| AI建议 | 显示最终方向、原始模型方向、邻居投票比例及过滤原因，不代替玩家移动 |
| AI托管 | 每个游戏更新周期推理，转换为方向控制并调用原来的Fish.move |

AI托管时按W/A/S/D会立即退回建议模式，把控制权交给玩家。暂停、死亡、胜利和返回主界面会停止游戏定时器。新一局沿用已选择的AI模式。画面沿用原工程1900×1000固定布局，小屏幕可能需要调整系统缩放或窗口布局。

## 从源码构建和一键测试

环境：JDK 8+（PATH内有java和javac）、Python 3.9+。无第三方Python依赖，JUnit/Hamcrest及许可已放入lib，离线可运行。

```console
python project.py build
python project.py test
```

Windows也可双击 `run-tests.bat`；macOS/Linux执行 `sh run-tests.command`。`python project.py game` 会重新构建后启动游戏。

`project.py test` 会重新编译源代码及测试，在 `target/ab-test-work` 隔离副本运行Java测试，再以Java后端执行AI用例；所有结果正确才返回0。排行榜不会被测试污染。此工程的“全部测试”以这条命令为准。

也可在IDE中导入pom.xml，运行fish.GameFrame；运行目录设为项目根目录。Maven环境可用时，`mvn test`执行56条Java测试，随后执行 `python ai-tests/run_tests.py --backend java` 运行30条AI用例。Maven首次解析插件可能需要网络，本机验证使用离线project.py路径，未声称实测Maven路径。

## 实测结果及证据

| 测试组成 | 数量 | 结果 |
|---|---:|---|
| 模块一原JUnit用例，源码未修改 | 36 | 全部通过 |
| Java模型、实际Fish移动、UI面板、暂停恢复等集成用例 | 20 | 全部通过 |
| 模块二AI用例（默认Java后端） | 30 | 全部通过 |
| 合计 | 86 | 0失败、0错误 |

30条AI用例中，AI01–27和AI30通过测试适配器调用实际Java推理类；AI28为Python协议层的故障注入补充测试，AI29为Python参考模型加载的损坏测试。Java模型损坏/缺失另由20条集成用例中的专门用例验证。不能把这30条都描述成直接测试Java模型。

- `reports/java-junit.log`：56条JUnit运行输出。
- `reports/java-ai-tests.log`：30条AI用例控制台输出。
- `reports/build-summary.json`：整体退出码、Java模型SHA-256。
- `ai-tests/reports/java-guarded/`：Java后端逐用例输入、输出、指标与JUnit XML。
- `reports/game-panel-preview.png`：实际FishUI在无窗口环境下绘制的面板截图。

已经验证源代码构建、真实模型推理、Swing面板绘制与游戏更新链路。尚未进行完整桌面窗口的人工点击试玩或通关测试。AI是演示型单步策略，不保证通关；模型准确率和公平性结论的范围见 `docs/AI_INTEGRATION.md`。

## 工程结构

```text
src/main/java/fish/             原游戏及Swing定时器GameLoop
src/main/java/fish/ai/          Java模型、控制器、测试协议入口
src/main/resources/ai/          Java直接加载的1600条拟合样本
src/main/resources/resource/    原游戏图片
src/main/resources/record/      原排行榜文件
src/test/java/handoff/          原模块一36条测试与支持代码
src/test/java/fish/ai/           20条Java集成测试
ai-tests/                      Python训练/参考实现、30条用例、Java测试适配器
scripts/export_model.py         将同一模型导出为Java资源
project.py                     离线构建、打包和全量测试入口
lib/                           JUnit/Hamcrest及许可
dist/                          已编译可运行JAR
reports/                       本次验证证据
docs/AI_INTEGRATION.md          架构、改动说明、限制与故障复现
docs/MODULE1_README.md          附件二原README，仅作为历史参考
```

## 重新训练或复现AI缺陷

```console
python ai-tests/train_model.py
python scripts/export_model.py
python project.py test
```

训练脚本采用固定种子合成专家示例，导出不改变权重。构建会比对JSON和TSV模型样本，发现不同就报错，避免游戏和测试各测一份不同模型。

模型忽略绝对边界的缺陷仍可独立复现，实际游戏始终启用安全过滤：

```console
python ai-tests/run_tests.py --backend java --variant raw --case test_AI22_left_boundary
python ai-tests/run_tests.py --backend java --case test_AI22_left_boundary
```

第一条预期返回1并产生失败断言；第二条验证过滤后通过。raw仅用于实验，不是界面中可切换的游戏模式。请保留失败证据。

模块一的旧报告和README描述的是旧版本；本项目另修复了其4项已记录问题，详情见改动说明。未替你们提交Git、编写成员贡献或提交课程成果，请按实际工作记录版本历史。
