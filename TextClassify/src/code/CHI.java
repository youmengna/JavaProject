package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CHI {
	/**
	 * 
	 * @param wordcontent
	 *            单词的内容
	 * @param wordCategory
	 *            单词类别
	 * @return
	 */
	public static void countWordCHI() {
		/**
		 * 遍历每一个单词，并统计CHI
		 */
		for (Word tempWord : LocalClass.getInstance().Vocabulary) {
			int a = 0, b = 0, c = 0, d = 0;
			String content = tempWord.getText();
			String[] category = tempWord.getCategory().split("\\\\");
			String wordCategory1 = category[category.length - 1];
			for (TextClass textClass : LocalClass.getInstance().classes) {
				String className = textClass.className;
				List<File> alltextFiles = textClass.alltext;
				if (wordCategory1.equals(className)) {
					for (File file : alltextFiles) {
						HashMap<String, Integer> hashMap = LocalClass.getInstance().normalTFAllFiles.get(file.getAbsolutePath());
						if (hashMap.containsKey(content)) {
							tempWord.setA(++a);//a +
						} else {
							tempWord.setC(++c);//c -
						}

					}

				} else {
					for (File file : alltextFiles) {

						HashMap<String, Integer> hashMap = LocalClass.getInstance().normalTFAllFiles.get(file.getAbsolutePath());
						if (hashMap.containsKey(content)) {
							tempWord.setB(++b);//b -
						} else {
							tempWord.setD(++d);//d +
						}

					}
				}
			}
			tempWord.wordCHI = (double) ((a * d - b * c) * (a * d - b * c)) / ((a + b) * (c + d));
			System.out.println("单词的名字=" + tempWord.getText() + ",单词类别=" + wordCategory1 + ",单词CHI=" + tempWord.wordCHI
					+ "a：" + a + " b:" + b + "  c:" + c + "  d:" + d);
		}
	}

	/**
	 * 对每类中的单词按照CHI值由大到小排序
	 */
	public static void sortWordCHI() {
		for (TextClass textClass : LocalClass.getInstance().classes) {
			Map<String, Word> tempHashMap = textClass.nkHashMap;
			List<Map.Entry<String, Word>> infoIds = new ArrayList<Map.Entry<String, Word>>(tempHashMap.entrySet());

			// 排序前
			for (int i = 0; i < infoIds.size(); i++) {
				String wordName = infoIds.get(i).getKey();
				double wordCHI = infoIds.get(i).getValue().wordCHI;
				System.out.println("CHI排序前的结果为：" + wordName + "=" + wordCHI);
			}
			// 对每个类中的单词按照wordCHI由大到小排序
			Collections.sort(infoIds, new Comparator<Map.Entry<String, Word>>() {
				public int compare(Map.Entry<String, Word> o1, Map.Entry<String, Word> o2) {
					double result = o2.getValue().wordCHI - o1.getValue().wordCHI;
					if (result > 0)
						return 1;
					else if (result < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			System.out.println("对"+textClass.className+"单词按照CHI值由大到小排序");
			// 排序后，并取得前featureNum个，放入CHInkHashMap中
			for (int i = 0; i < LocalClass.getInstance().featureNum*infoIds.size(); i++) {
				String wordName = infoIds.get(i).getKey();
				double wordCHI = infoIds.get(i).getValue().wordCHI;
				Word word = infoIds.get(i).getValue();
				textClass.CHInkHashMap.put(wordName, word);
				System.out.println("CHI排序后的结果为：" + wordName + "=" + wordCHI);
			}
		}
		for (TextClass class1 : LocalClass.getInstance().classes) {
//			System.out.println("未进行开方检验"+class1.className + "取"
//					+ class1.nkHashMap.size() + "个词");
			System.out.println("经过开方检验"+class1.className + "取"
					+ class1.CHInkHashMap.size() + "个词");
		}
	}
}
