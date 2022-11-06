#-*- encoding:utf-8 -*-
from __future__ import print_function

import os
import sys
sys.path.append(os.path.dirname(sys.path[0]))

# try:
#     reload(sys)
#     sys.setdefaultencoding('utf-8')
# except:
#     pass

from textrank4zh import TextRank4Sentence

text = sys.argv[1]
top_n_senteces = ord(sys.argv[2]) - ord('0')

tr4s = TextRank4Sentence()
tr4s.analyze(text=text, lower=True, source = 'all_filters')

result = []
for item in tr4s.get_key_sentences(num=top_n_senteces):
    result.append(item.sentence)
print("".join(result))