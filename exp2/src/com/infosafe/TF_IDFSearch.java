package com.infosafe;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TF_IDFSearch {
    private Map<Integer, Map<String, Integer>> corpus = new HashMap<>();
    private List<String> lineList = new ArrayList<>();
    private Map<String, Integer> dic = new HashMap<>();

    //读取语料库并计算各文档词频
    private void readCorpus() throws IOException {
        String fileName = "resource/语料库.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            lineList.add(line);
        }
        br.close();

        //计算每个文档的词频
        for (int i = 0; i < lineList.size(); i++) {
            Map<String, Integer> words = new HashMap<>();
            String[] arrays = lineList.get(i).trim().split(" +");
            for (String s : arrays) {
                if (s.indexOf("19980") != -1)
                    continue;
                String[] endString = s.split("/");
                s = endString[0];
                int count = (words.containsKey(s)) ? words.get(s) : 0;
                words.put(s, count + 1);
            }
            corpus.put(i, words);
        }
    }

    //读取实验1中计算生成的词表
    private void readDic() throws IOException {
        String fileName = "resource/dic.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] input = line.trim().split(":");
            dic.put(input[0], Integer.valueOf(input[1]));
        }
        br.close();
    }

    //计算各文档的TF-IDF值并进行排序取top10
    private void TF_IDFCalculate(String line) throws IOException {
        readDic();
        List<String> divideWords = new ArrayList<>();

        //BMM
        for (int j = line.length(); j >= 0; j--) {
            for (int i = 0; i < j; i++) {
                String string = line.substring(i, j);
                if (dic.containsKey(string) && !string.equals(" ")) {
                    divideWords.add(string);
                    j -= string.length() - 1;
                    break;
                } else if (i == j - 1 && !string.equals(" ")) {
                    divideWords.add(string);
                    j -= string.length() - 1;
                }
            }
        }

        // 获取TF
        Map<Integer, Map<Integer, Double>> wordTF = new HashMap<>();
        for (int i = 0; i < lineList.size(); i++) {
            Map<String, Integer> map = corpus.get(i);
            Map map_tf = new HashMap();

            for (int j = 0; j < divideWords.size(); j++) {
                int num = map.containsKey(divideWords.get(j)) ? map.get(divideWords.get(j)) : 0;
                double temp = (double) num / map.size();
                map_tf.put(j, temp);
                wordTF.put(i, map_tf);
            }
        }


        // 获取IDF
        double[] wordIDF = new double[divideWords.size()];
        for (int i = 0; i < divideWords.size(); i++) {
            int idf = 0;
            for (int j = 0; j < lineList.size(); j++) {
                if (lineList.get(j).contains(divideWords.get(i))) {
                    idf++;
                }
            }
            if (idf == 0) {
                System.out.println(idf + "所有文档中未出现，无法进行运算！");
                System.exit(-1);
            }
            double temp = lineList.size() / idf;
            wordIDF[i] = Math.log(temp);
        }

        // 计算TF-IDF
        Map<Integer, Double> tf_idf = new HashMap<>();
        for (int i = 0; i < lineList.size(); i++) {
            double result = 0.0;
            for (int j = 0; j < divideWords.size(); j++) {
                Double map = wordTF.get(i).get(j);
                result += map * wordIDF[j];
            }
            tf_idf.put(i, result);
        }

        // 排序取前10并输出
        Collections.reverse(divideWords);
        System.out.println("输入的分词：" + divideWords);
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(tf_idf.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < 10; i++) {
            int a = entryList.get(i).getKey();
            System.out.println((i + 1) + ".TF-IDF:" + entryList.get(i).getValue());
            System.out.println("文本为：" + lineList.get(a) + "\n");
        }

    }

    public static void main(String[] args) throws IOException, ScriptException {
        TF_IDFSearch tf_idfSearch = new TF_IDFSearch();
        //计算各文档词频并存储
        tf_idfSearch.readCorpus();
        Scanner scanner = new Scanner(System.in);
        //接收多次输入
        //while (scanner.hasNext()) {
        String input = scanner.nextLine();
        input.getBytes("UTF-8");
        tf_idfSearch.TF_IDFCalculate(input);
        //}
    }
}
