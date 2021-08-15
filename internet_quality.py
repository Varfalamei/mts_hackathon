import json
from config import *


with open('data/mts.json', 'r') as f:
    mts = json.loads(f.read())

with open('data/beeline.json', 'r') as f:
    beeline = json.loads(f.read())

with open('data/yota.json', 'r') as f:
    yota = json.loads(f.read())

operators = ['mts', 'beeline', 'yota']

for g in qual:
    for z in zoom:
        for i in range(len(mts[g][z])):
            lis = [mts[g][z][i][3], beeline[g][z][i][3], yota[g][z][i][3]]
            max_opr = lis.index(max(lis))
            if max(lis) == 0:
                mts[g][z][i].append('no Internet')
            else:
                mts[g][z][i].append(operators[max_opr])
                mts[g][z][i].append(max(lis))

with open(f'data/mts_quality_best_operator.json', 'w', encoding='utf-8') as f:
    json.dump(mts, f, ensure_ascii=False, indent=4)