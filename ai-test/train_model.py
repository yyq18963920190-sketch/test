"""Reproducible synthetic imitation-learning data; test inputs are not read here."""
import hashlib
import json
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def train():
    rng = random.Random(20260920)
    samples = []
    for threat in (False, True):
        for _ in range(800):
            dx, dy = rng.uniform(-1800, 1800), rng.uniform(-900, 900)
            tx, ty = (-dx, -dy) if threat else (dx, dy)
            label = ('RIGHT' if tx > 0 else 'LEFT') if abs(tx) >= abs(ty) else ('DOWN' if ty > 0 else 'UP')
            samples.append([dx/1900, dy/1900, 2.0 if threat else -2.0, label])
    doc = {'schema': 1, 'k': 3, 'seed': 20260920,
           'description': 'Synthetic expert-labelled imitation learning, not gameplay recordings',
           'features': ['dx/1900', 'dy/1900', 'threat:+2, prey:-2'], 'samples': samples}
    path = ROOT / 'models/policy.json'
    path.parent.mkdir(exist_ok=True)
    path.write_text(json.dumps(doc, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
    print(f'Fitted {len(samples)} samples; sha256={hashlib.sha256(path.read_bytes()).hexdigest()}')


if __name__ == '__main__':
    train()
