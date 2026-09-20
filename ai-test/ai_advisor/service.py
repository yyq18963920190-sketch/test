"""Snapshot validation -> learned prediction -> one-step safety filter.

Safety uses conservative rectangles, not an assertion that the original Java
collision algorithm is identical. Other fish are stationary for this one step.
"""
import json
import math
from .model import KNN, ACTIONS

DELTAS = {'LEFT': (-1, 0), 'RIGHT': (1, 0), 'UP': (0, -1),
          'DOWN': (0, 1), 'STAY': (0, 0)}


def number(value, low, high):
    return type(value) in (int, float) and math.isfinite(value) and low <= value <= high


def validate(s):
    if not isinstance(s, dict):
        return False
    p, fishes = s.get('player'), s.get('fish')
    if not isinstance(p, dict) or not isinstance(fishes, list) or len(fishes) > 200:
        return False
    if not all(number(p.get(k), lo, hi) for k, lo, hi in (
        ('x', 0, 1900), ('y', 0, 980), ('width', 1, 220), ('height', 1, 150),
        ('speed', 0, 100), ('grade', 1, 4))):
        return False
    if type(p['grade']) is not int or type(p.get('state')) is not int or p['state'] not in (0, 1, 2, 3, 4):
        return False
    if p['x'] + p['width'] > 1900 or p['y'] + p['height'] > 980:
        return False
    for f in fishes:
        if not isinstance(f, dict) or not all(number(f.get(k), lo, hi) for k, lo, hi in (
            ('x', -300, 2200), ('y', -150, 1130), ('width', 1, 220),
            ('height', 1, 150), ('grade', 1, 4))):
            return False
        if type(f['grade']) is not int or type(f.get('state')) is not int or f['state'] not in (0, 1):
            return False
    return True


def position(p, action):
    dx, dy = DELTAS[action]
    return p['x'] + dx*p['speed'], p['y'] + dy*p['speed']


def legal(p, action):
    x, y = position(p, action)
    return 0 <= x and x+p['width'] <= 1900 and 0 <= y and y+p['height'] <= 980


def overlaps(p, f, action):
    x, y = position(p, action)
    return (x < f['x']+f['width'] and x+p['width'] > f['x']
            and y < f['y']+f['height'] and y+p['height'] > f['y'])


class Advisor:
    def __init__(self, model=None, guarded=True):
        self.model = model if model is not None else KNN()
        self.guarded = guarded

    def advise(self, snapshot):
        result = {'action': 'STAY', 'raw_action': None, 'confidence': 0.0,
                  'reason': 'invalid_input'}
        if not validate(snapshot):
            return result
        p = snapshot['player']
        if p['state'] != 1:
            result['reason'] = 'inactive'
            return result
        live = [f for f in snapshot['fish'] if f['state'] == 1]
        threats = [f for f in live if f['grade'] < p['grade']]
        targets = threats or live
        if not targets:
            result['reason'] = 'empty_scene'
            return result
        # Canonical tie-breaking ensures input array order is irrelevant.
        target = min(targets, key=lambda f: (
            (f['x']-p['x'])**2+(f['y']-p['y'])**2,
            f['x'], f['y'], f['grade'], f['width'], f['height']))
        features = ((target['x']-p['x'])/1900, (target['y']-p['y'])/1900,
                    2.0 if threats else -2.0)
        raw, confidence = self.model.predict(features)
        if raw not in ACTIONS or not number(confidence, 0, 1):
            result['reason'] = 'invalid_model_output'
            return result
        result.update(action=raw, raw_action=raw, confidence=confidence, reason='model')
        if not self.guarded:
            return result
        safe = [a for a in ACTIONS if legal(p, a)
                and not any(overlaps(p, f, a) for f in threats)]
        if raw in safe:
            return result
        if not safe:
            result.update(action='STAY', reason='no_safe_action')
            return result
        def clearance(a):
            x, y = position(p, a)
            return min(((x+p['width']/2-f['x']-f['width']/2)**2
                        +(y+p['height']/2-f['y']-f['height']/2)**2
                        for f in threats), default=0)
        result.update(action=max(safe, key=clearance), reason='safety_override')
        return result


def main():
    import argparse
    from pathlib import Path
    parser = argparse.ArgumentParser(description='Offline AI game action advisor')
    parser.add_argument('snapshot', type=Path)
    args = parser.parse_args()
    try:
        snapshot = json.loads(args.snapshot.read_text(encoding='utf-8'))
        print(json.dumps(Advisor().advise(snapshot), ensure_ascii=False))
    except (OSError, ValueError, KeyError) as exc:
        parser.exit(2, f'Input/model error: {exc}\n')


if __name__ == '__main__':
    main()
