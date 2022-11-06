import os
import sys
sys.path.append(os.path.dirname(sys.path[0]))
import jieba
from wordcloud import WordCloud
import matplotlib.pyplot as plt

param_photo_name = sys.argv[1]

param_text = sys.argv[2]
param_width = sys.argv[3]
param_height = sys.argv[4]
param_color = sys.argv[5]
param_directory = sys.argv[6]
param_stop_words = sys.argv[7]
param_font_path = sys.argv[8]

stopwords = []
with open(param_stop_words, encoding="utf-8") as input_file:
    for line in input_file.readlines():
        line = line.strip('\n')
        stopwords.append(line)

segments = jieba.cut(param_text)
word_frequency = dict()

for segment in segments:
    if segment in stopwords:
        continue
    word_frequency[segment] = word_frequency[segment] + 1 if word_frequency.__contains__(segment) else 1

wordcloud = WordCloud(
    background_color=param_color,
    font_path=param_font_path,
    height=int(param_height), 
    width=int(param_width),
).generate_from_frequencies(word_frequency)

plt.imshow(wordcloud)
plt.axis("off")
absolute_path = param_directory + "/" + param_photo_name + ".png"
wordcloud.to_file(absolute_path)
print(absolute_path)