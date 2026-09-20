"""Small auditable kNN classifier. Inference reads fitted examples, not labels from an oracle."""
import hashlib
import json
import math
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ACTIONS = ('LEFT', 'RIGHT', 'UP', 'DOWN', 'STAY')


class KNN:
    def __init__(self, path=None):
        path = Path(path or ROOT / 'models/policy.json')
        content = path.read_bytes()
        self.sha256 = hashlib.sha256(content).hexdigest()
        doc = json.loads(content)
        if doc.get('schema') != 1 or doc.get('k') != 3:
            raise ValueError('unsupported model schema/k')
        self.rows = doc['samples']
        if not isinstance(self.rows, list) or len(self.rows) < 3:
            raise ValueError('insufficient fitted samples')
        for row in self.rows:
            if (not isinstance(row, list) or len(row) != 4
                    or row[3] not in ACTIONS
                    or any(type(v) not in (int, float) or not math.isfinite(v) for v in row[:3])):
                raise ValueError('invalid model sample')

    def predict(self, features):
        nearest = sorted(enumerate(self.rows), key=lambda item: (
            sum((a-b)**2 for a, b in zip(features, item[1][:3])), item[0]))[:3]
        votes = {a: sum(row[3] == a for _, row in nearest) for a in ACTIONS}
        action = max(ACTIONS, key=lambda a: votes[a])
        return action, votes[action] / 3.0
