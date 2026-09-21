# 集成工程的AI测试与Python参考实现

当前版本已经实际接入Java游戏，完整运行方式以项目根目录README.md为准。

在根目录执行 `python project.py test`：重新编译Java，然后执行原36条测试、20条Java集成测试和本目录30条AI用例。

单独运行（须先在根目录执行 `python project.py build`）：

```console
python ai-tests/run_tests.py --backend java
python ai-tests/run_tests.py --backend java --variant raw
python ai-tests/run_tests.py --backend python
```

Java后端的AI01–27及AI30通过java_backend.py启动AdvisorCli，调用和游戏相同的AiAdvisor。AI28/29仍是Python协议/模型文件的补充检查；Java模型损坏与缺失另在AiIntegrationTest验证。测试适配器不会把Python预测作为Java响应。

java-guarded报告是实际接入版本的主要证据；原guarded/raw目录保留上次独立Python工程的参考结果。docs中的设计、用例与缺陷资料也描述Python参考实现；当前Java严格边界、独立纵向速度与实际游戏集成见根目录docs/AI_INTEGRATION.md。

本目录的AI建议器CLI `python -m ai_advisor.service data/example.json` 仍可在本目录内独立运行，但游戏不调用它。训练后需要回根目录执行 `python scripts/export_model.py`，再构建测试，保证两种模型一致。
