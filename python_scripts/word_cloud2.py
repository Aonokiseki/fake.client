import os
import sys
sys.path.append(os.path.dirname(sys.path[0]))
import jieba
from wordcloud import WordCloud, ImageColorGenerator
import matplotlib.pyplot as plt
import imageio

param_photo_name = "com3d2"
param_text = "且说宝玉因见林黛玉又病了，心里放不下，饭也懒去吃，不时来问。黛玉又怕他有个好歹，因说道：“你只管看你的戏去，在家里作什么？”宝玉因昨日张道士提亲，心中大不受用，今听见黛玉如此说，因想道：“别人不知道我的心还可恕，连她也奚落起我来。”因此心中更比往日的烦恼加了百倍。若是别人跟前，断不能动这肝火，只是黛玉说了这话，倒比往日别人说这话不同，由不得立刻沉下脸来道：“我白认得了你。罢了，罢了！”林黛玉听说，便冷笑了两声，“我也知道白认得了我，我哪里像人家，有什么配得上呢！”宝玉听了，便向前来直问到脸上：“你这么说，是安心咒我天诛地灭？”黛玉一时解不过这话来。宝玉又道：“昨儿我还为这个赌了几回咒，今儿你到底又准我一句。我便天诛地灭，你又有什么益处？”黛玉一闻此言，方想起上日的话来。今日原是自己说错了，又是着急，又是羞愧，便颤颤兢兢的说道：“我要安心咒你，我也天诛地灭。何苦来！我知道，昨日张道士说亲，你怕阻了你的好姻缘，你心里生气，来拿我来煞性子。”"
param_color = "white"
param_directory = "E:/Download"
param_stop_words = "E:/Download/fake.client/dict/stopwords.txt"
param_font_path = "C:/Windows/Fonts/simsun.ttc"

_mask = imageio.imread("E:/Game/CustomOrderMaid3D2_5/Thumb/35580acd-481f-45e3-9faf-54ff905ebe9c.png")
_image_colors = ImageColorGenerator(_mask)

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

_wordcloud = WordCloud(
    background_color=param_color,
    font_path=param_font_path,
    mask=_mask
)
_wordcloud.generate_from_frequencies(word_frequency)
_wordcloud.recolor(color_func=_image_colors)

plt.imshow(_wordcloud)
plt.axis("off")
absolute_path = param_directory + "/" + param_photo_name + ".png"
_wordcloud.to_file(absolute_path)
print(absolute_path)