"""Offline build/test entry point. Python stdlib + JDK 8+, no Maven download needed."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parent


def run(command, cwd=ROOT, logfile=None):
    print('> '+' '.join(str(x) for x in command), flush=True)
    result = subprocess.run([str(x) for x in command], cwd=cwd, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, text=True, encoding='utf-8', errors='replace')
    print(result.stdout, end='')
    if logfile:
        logfile.parent.mkdir(parents=True, exist_ok=True)
        logfile.write_text(result.stdout, encoding='utf-8')
    return result.returncode


def compile_java(sources, target, classpath=None):
    target.mkdir(parents=True, exist_ok=True)
    # Remove only stale class outputs inside this project's known build directory.
    target = target.resolve()
    if ROOT/'target' not in target.parents:
        raise RuntimeError('Refusing to clean an unexpected class directory')
    for compiled in target.rglob('*.class'):
        compiled.unlink()
    cmd = ['javac', '-J-Dfile.encoding=UTF-8', '-encoding', 'UTF-8', '-source', '8', '-target', '8', '-Xlint:-options', '-d', target]
    if classpath:
        cmd += ['-cp', classpath]
    cmd += sorted(sources.rglob('*.java'))
    return run(cmd)


def build():
    if not shutil.which('javac'):
        raise RuntimeError('Install JDK 8+ and add javac/java to PATH.')
    # Assert Java model is exactly the Python artifact, without retraining during tests.
    doc = json.loads((ROOT/'ai-tests/models/policy.json').read_text(encoding='utf-8'))
    lines = (ROOT/'src/main/resources/ai/policy.tsv').read_text(encoding='utf-8').splitlines()
    assert lines[0] == 'deep-sea-knn-v1\tk=3'
    rows = [[float(v) for v in parts[:3]]+[parts[3]] for parts in (line.split('\t') for line in lines[1:])]
    if rows != doc['samples']:
        raise RuntimeError('Java/Python model mismatch. Run scripts/export_model.py after retraining.')
    if compile_java(ROOT/'src/main/java', ROOT/'target/classes'):
        return 1
    # Package only classes produced from the current source tree plus resources.
    dest = ROOT/'dist/deep-sea-growth-ai.jar'
    dest.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(dest, 'w', zipfile.ZIP_DEFLATED) as jar:
        jar.writestr('META-INF/MANIFEST.MF', 'Manifest-Version: 1.0\r\nMain-Class: fish.GameFrame\r\n\r\n')
        for base in (ROOT/'target/classes', ROOT/'src/main/resources'):
            for file in sorted(base.rglob('*')):
                if file.is_file():
                    jar.write(file, file.relative_to(base).as_posix())
    print('Built:', dest)
    return 0


def test():
    if build():
        return 1
    libs = sorted((ROOT/'lib').glob('*.jar'))
    cp = os.pathsep.join(str(p) for p in [ROOT/'target/classes', ROOT/'src/main/resources']+libs)
    if compile_java(ROOT/'src/test/java', ROOT/'target/test-classes', cp):
        return 1
    isolated = ROOT/'target/ab-test-work'
    shutil.copytree(ROOT/'src/main/resources', isolated/'src/main/resources', dirs_exist_ok=True)
    cp = os.pathsep.join([str(ROOT/'target/test-classes'), str(ROOT/'src/test/resources'), cp])
    classes = ['handoff.a.AMovementTest', 'handoff.a.AUpgradeTest', 'handoff.a.AWinTest',
               'handoff.b.BDaoJuTest', 'handoff.b.BFishCollisionTest', 'handoff.b.BTimeTest',
               'fish.ai.AiIntegrationTest']
    (ROOT/'reports').mkdir(exist_ok=True)
    java_exit = run(['java', '-Dfile.encoding=UTF-8', '-Djava.awt.headless=true',
                     '-Dai.preview.path='+str(ROOT/'reports/game-panel-preview.png'),
                     '-Dab.test.workdir='+str(isolated), '-cp', cp, 'org.junit.runner.JUnitCore']+classes,
                    cwd=isolated, logfile=ROOT/'reports/java-junit.log')
    ai_exit = run([sys.executable, ROOT/'ai-tests/run_tests.py', '--backend', 'java'],
                  cwd=ROOT/'ai-tests', logfile=ROOT/'reports/java-ai-tests.log')
    summary = {'java_junit_exit': java_exit, 'java_ai_exit': ai_exit,
               'model_sha256': hashlib.sha256((ROOT/'src/main/resources/ai/policy.tsv').read_bytes()).hexdigest()}
    (ROOT/'reports/build-summary.json').write_text(json.dumps(summary, indent=2), encoding='utf-8')
    return 1 if java_exit or ai_exit else 0


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('command', choices=['build', 'test', 'game'], nargs='?', default='test')
    args = parser.parse_args()
    try:
        if args.command == 'test': return test()
        if args.command == 'build': return build()
        if build(): return 1
        return subprocess.call(['java', '-jar', str(ROOT/'dist/deep-sea-growth-ai.jar')], cwd=ROOT)
    except (OSError, RuntimeError, ValueError, AssertionError) as exc:
        print('ERROR:', exc, file=sys.stderr)
        return 2


if __name__ == '__main__':
    sys.exit(main())
