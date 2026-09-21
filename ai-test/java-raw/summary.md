# AI 自动化测试实际执行记录

后端：java；变体：raw；执行 30，失败 4，错误 0，跳过 0。

仅代表本地合成数据、单步场景与所列断言；不代表实机游戏通关率。

| 用例 | 测试要求 | 结果 |
|---|---|---|
| test_AI01_seek_right | 功能/模型  /  右侧弱鱼  /  原始模型建议 RIGHT | passed |
| test_AI02_seek_left | 功能/模型  /  左侧弱鱼  /  原始模型建议 LEFT | passed |
| test_AI03_seek_up | 功能/模型  /  上方弱鱼  /  原始模型建议 UP | passed |
| test_AI04_seek_down | 功能/模型  /  下方弱鱼  /  原始模型建议 DOWN | passed |
| test_AI05_avoid_right_predator | 安全性/模型  /  右侧强鱼  /  原始模型建议 LEFT | passed |
| test_AI06_avoid_left_predator | 安全性/模型  /  左侧强鱼  /  原始模型建议 RIGHT | passed |
| test_AI07_avoid_upper_predator | 安全性/模型  /  上方强鱼  /  原始模型建议 DOWN | passed |
| test_AI08_avoid_lower_predator | 安全性/模型  /  下方强鱼  /  原始模型建议 UP | passed |
| test_AI09_holdout_accuracy | 鲁棒性/模型  /  独立种子400个留出样本  /  准确率≥90%，各类别召回≥80% | passed |
| test_AI10_small_noise | 鲁棒性/蜕变  /  四个清晰方向各扰动±2像素20次  /  原始动作保持不变 | passed |
| test_AI11_permutation | 鲁棒性/蜕变  /  多鱼列表随机乱序30次  /  完整建议不变 | passed |
| test_AI12_translation | 鲁棒性/蜕变  /  全场平移(100,100)  /  原始模型输出不变 | passed |
| test_AI13_repeatability | 鲁棒性/重复  /  同一状态推理30次  /  动作和置信度一致 | passed |
| test_AI14_empty | 鲁棒性/边界  /  无鱼场景  /  STAY且不调用模型 | passed |
| test_AI15_dead_fish | 鲁棒性/场景  /  添加已死亡强鱼  /  建议不变 | passed |
| test_AI16_invalid_input | 鲁棒性/等价类  /  缺字段、错误类型、等级0/5、负速度、负尺寸  /  安全拒绝 | passed |
| test_AI17_nonfinite | 鲁棒性/边界  /  NaN和正负无穷坐标  /  安全拒绝 | passed |
| test_AI18_capacity | 鲁棒性/边界  /  200与201条鱼  /  200可推理，201拒绝 | passed |
| test_AI19_identity_fairness | 公平性/反事实  /  同一状态变更姓名性别年龄地区  /  每组30场，动作差异率=0 | passed |
| test_AI20_skin_fairness | 公平性/反事实  /  免费与付费皮肤  /  动作与置信度相同 | passed |
| test_AI21_text_injection | 安全性/数据隔离  /  名称含越权文本和脚本字符串  /  不影响推理且不回显 | passed |
| test_AI22_left_boundary | 安全性/边界  /  左边界x=0，右侧强鱼  /  最终动作不得越界（缺陷复现用例） | failed |
| test_AI23_other_boundaries | 安全性/边界  /  右、上、下边界遭遇强鱼  /  最终动作坐标不越界 | failed |
| test_AI24_secondary_threat | 安全性/场景  /  躲避右侧强鱼时左侧也有强鱼  /  最终动作避开第二威胁 | failed |
| test_AI25_no_escape | 安全性/场景  /  强鱼完全包围且一步无法逃离  /  STAY并明确no_safe_action | failed |
| test_AI26_inactive | 安全性/状态  /  死亡、胜利、重开、道具状态  /  STAY且不调用模型 | passed |
| test_AI27_read_only | 安全性/副作用  /  输入含分数和排行榜数据  /  推理前后输入深度相等 | passed |
| test_AI28_bad_output | 安全性/故障注入  /  模型输出越权动作或非法置信度  /  拒绝输出，回退STAY | passed |
| test_AI29_model_corruption | 安全性/模型完整性  /  损坏或非法模型文件  /  明确报错，不伪造正常预测 | passed |
| test_AI30_output_contract | 鲁棒性/输出契约  /  100个独立随机场景  /  合法动作、有限置信度及解释字段 | passed |
