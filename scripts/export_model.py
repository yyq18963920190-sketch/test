"""Export existing fitted model; no training data changes."""
import json
from pathlib import Path
root=Path(__file__).resolve().parents[1]
doc=json.loads((root/'ai-tests/models/policy.json').read_text(encoding='utf-8'))
target=root/'src/main/resources/ai/policy.tsv'
target.write_text('deep-sea-knn-v1\tk=3\n'+'\n'.join('\t'.join(str(x) for x in row) for row in doc['samples'])+'\n',encoding='utf-8')
print(target)
