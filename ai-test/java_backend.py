"""Test adapter to the native Java advisor used by the game; no mocked predictions."""
import atexit
import os
from pathlib import Path
import subprocess
from ai_advisor.model import KNN
from ai_advisor.service import Advisor as PythonAdvisor

ROOT = Path(__file__).resolve().parents[1]
_process = None


def close():
    global _process
    if _process is not None:
        _process.stdin.close()
        try:
            _process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            _process.kill()
            _process.wait()
        _process.stdout.close()
        _process = None


atexit.register(close)


def request(line):
    global _process
    if _process is None:
        cp = os.pathsep.join([str(ROOT/'target/classes'), str(ROOT/'src/main/resources')])
        _process = subprocess.Popen(['java', '-Djava.awt.headless=true', '-cp', cp, 'fish.ai.AdvisorCli'],
                                    stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                    text=True, encoding='utf-8', bufsize=1)
    _process.stdin.write(line+'\n')
    _process.stdin.flush()
    response = _process.stdout.readline().strip()
    if not response:
        raise RuntimeError('Java advisor exited without a response; build the root project first')
    action, raw, confidence, reason = response.split('\t')
    return {'action': action, 'raw_action': None if raw == 'null' else raw,
            'confidence': float(confidence), 'reason': reason}


def numeric(value):
    # Explicit type encoding: never coerce bool/string into a valid number.
    return str(value) if type(value) in (int, float) else 'INVALID_TYPE'


def encode(s, guarded):
    try:
        if not isinstance(s, dict) or not isinstance(s['player'], dict) or not isinstance(s['fish'], list):
            return 'INVALID'
        p = s['player']
        player = ','.join(numeric(v) for v in (p['x'], p['y'], p['width'], p['height'], p['speed'],
                                               p.get('y_speed', p['speed']), p['grade'], p['state']))
        rows = []
        for f in s['fish']:
            rows.append(','.join(numeric(f[k]) for k in ('x', 'y', 'width', 'height', 'grade', 'state')))
        return ('GUARDED' if guarded else 'RAW')+'|'+player+'|'+';'.join(rows)
    except (KeyError, TypeError):
        return 'INVALID'


class Advisor:
    def __init__(self, model=None, guarded=True):
        self.guarded = guarded
        # AI28 intentionally tests injected Python protocol violations. Production
        # Java Action is an enum; Java corruption tests are in AiIntegrationTest.
        self.injected = PythonAdvisor(model, guarded) if model is not None and not isinstance(model, KNN) else None

    def advise(self, snapshot):
        if self.injected is not None:
            return self.injected.advise(snapshot)
        return request(encode(snapshot, self.guarded))
