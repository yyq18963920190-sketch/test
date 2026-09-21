"""One command, real unittest verdicts, JSON/Markdown/JUnit XML artifacts."""
import argparse
import datetime
import hashlib
import json
import os
import platform
import sys
import time
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def serializable(value):
    # JSON standard forbids NaN/Infinity; preserve these test inputs as strings.
    import math
    if isinstance(value, float) and not math.isfinite(value):
        return repr(value)
    if isinstance(value, dict):
        return {k: serializable(v) for k, v in value.items()}
    if isinstance(value, (list, tuple)):
        return [serializable(v) for v in value]
    return value


class Result(unittest.TextTestResult):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.records = []

    def startTest(self, test):
        super().startTest(test)
        self.started = time.perf_counter()

    def record(self, test, status, detail=''):
        self.records.append({'id': test.id(), 'description': test.shortDescription(),
                             'status': status, 'seconds': time.perf_counter()-self.started,
                             'detail': detail, 'evidence': getattr(test, 'evidence', [])})

    def addSuccess(self, test):
        super().addSuccess(test); self.record(test, 'passed')

    def addFailure(self, test, err):
        super().addFailure(test, err); self.record(test, 'failed', self._exc_info_to_string(err, test))

    def addError(self, test, err):
        super().addError(test, err); self.record(test, 'error', self._exc_info_to_string(err, test))

    def addSkip(self, test, reason):
        super().addSkip(test, reason); self.record(test, 'skipped', reason)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--variant', choices=['guarded', 'raw'], default='guarded')
    parser.add_argument('--case', help='method name, e.g. test_AI22_left_boundary')
    parser.add_argument('--backend', choices=['python', 'java'], default='java')
    args = parser.parse_args()
    if args.backend == 'java':
        import tests.test_ai as test_module
        import java_backend
        test_module.Advisor = java_backend.Advisor
    os.environ['AI_TEST_VARIANT'] = args.variant
    # Resolve resources relative to this file; invocation directory is irrelevant.
    from tests.test_ai import AITests
    suite = (unittest.defaultTestLoader.loadTestsFromName(args.case, AITests) if args.case
             else unittest.defaultTestLoader.loadTestsFromTestCase(AITests))
    result = unittest.TextTestRunner(verbosity=2, resultclass=Result).run(suite)
    folder = ROOT / 'reports' / (args.backend+'-'+args.variant + ('-'+args.case if args.case else ''))
    folder.mkdir(parents=True, exist_ok=True)
    model = ROOT / 'models/policy.json'
    report = {'timestamp_utc': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'python': sys.version, 'platform': platform.platform(), 'variant': args.variant,
              'model_sha256': hashlib.sha256(model.read_bytes()).hexdigest() if model.is_file() else None,
              'backend': args.backend, 'command': sys.argv, 'total': result.testsRun,
              'failures': len(result.failures), 'errors': len(result.errors),
              'skipped': len(result.skipped), 'cases': result.records}
    (folder/'results.json').write_text(json.dumps(serializable(report), ensure_ascii=False, indent=2, allow_nan=False), encoding='utf-8')
    xml = ET.Element('testsuite', name='AIAdvisor-'+args.variant, tests=str(result.testsRun),
                     failures=str(len(result.failures)), errors=str(len(result.errors)), skipped=str(len(result.skipped)))
    for row in result.records:
        case = ET.SubElement(xml, 'testcase', name=row['id'], time=f"{row['seconds']:.6f}")
        tag = {'failed': 'failure', 'error': 'error', 'skipped': 'skipped'}.get(row['status'])
        if tag:
            ET.SubElement(case, tag).text = row['detail']
    ET.ElementTree(xml).write(folder/'junit.xml', encoding='utf-8', xml_declaration=True)
    lines = ['# AI 自动化测试实际执行记录', '',
             f"后端：{args.backend}；变体：{args.variant}；执行 {result.testsRun}，失败 {len(result.failures)}，错误 {len(result.errors)}，跳过 {len(result.skipped)}。",
             '', '仅代表本地合成数据、单步场景与所列断言；不代表实机游戏通关率。', '',
             '| 用例 | 测试要求 | 结果 |', '|---|---|---|']
    for row in result.records:
        lines.append(f"| {row['id'].split('.')[-1]} | {(row['description'] or '').replace('|', ' / ')} | {row['status']} |")
    (folder/'summary.md').write_text('\n'.join(lines)+'\n', encoding='utf-8')
    print(f'Artifacts: {folder}')
    return 0 if result.wasSuccessful() and result.testsRun > 0 and not result.skipped else 1


if __name__ == '__main__':
    sys.exit(main())
