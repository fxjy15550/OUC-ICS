import re


# 扩充词典
def addtodic(dic, s):
    if dic.__contains__(s):
        dic[s] += 1
    else:
        dic[s] = 1


# 由语料库生成词典
def create(dic, data):
    p = re.compile(u'[\u4E00-\u9FA5]+')
    l = p.findall(data)
    for i in l:
        addtodic(dic, i)


# 前向分词算法
def fmm(sentence, dic):
    result = ""
    succ = 0
    maxlength = 32
    while not len(sentence) is 0:
        temp_s = sentence[:maxlength]
        while not dic.__contains__(temp_s):
            if len(temp_s) > 1:
                temp_s = temp_s[:-1]
            else:
                break
        result += temp_s + '/'
        sentence = sentence[len(temp_s):]
    return result


# 后向分词算法
def bmm(sentence, dic):
    result = []
    succ = 0
    maxlength = 32
    while not len(sentence) is 0:
        temp_s = sentence[len(sentence) - maxlength:]
        while not dic.__contains__(temp_s):
            if len(temp_s) > 1:
                temp_s = temp_s[1:]
            else:
                break
        result.append(temp_s)
        sentence = sentence[:len(sentence) - len(temp_s)]
    res = "".join(s + '/' for s in reversed(result))
    return res


if __name__ == '__main__':
    file = open("./语料库.txt", 'r', encoding="utf-8")
    data = file.read()
    data = data[3:]
    dic = {}
    create(dic, data)
    l = sorted(dic.items(), key=lambda item: item[1])
    outfile = open('./dic.txt', 'w')
    for i in l:
        s = "" + i[0] + ":" + str(i[1]) + "\n"
        outfile.write(s)
    outfile.close()
    file = open("./例句.txt", 'r', encoding="utf-8")
    passage = file.read()
    print("原文：")
    print(passage)
    print("fmm:")
    file.seek(0)
    while 1:
        line = file.readline()
        if not line:
            break
        print(fmm(line.strip("\r\n"), dic))
    print("bmm:")
    file.seek(0)
    while 1:
        line = file.readline()
        if not line:
            break
        print(bmm(line.strip("\r\n"), dic))
