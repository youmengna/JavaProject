package code;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class TextClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 每类中分对的文本数
	 */
	public float a = 0;
	/**
	 * 
	 */
	public float b = 0;
	/**
	 * 
	 */
	public float c = 0;

	public TextClass() {
		this.CHInkHashMap = new HashMap<String, Word>();
	}

	/**
	 * @param args
	 */
	/**
	 * 类别名称
	 */
	public String className;
	/**
	 * 该类所有文档
	 */
	public List<File> alltext;

	public HashMap<String, HashMap<String, Integer>> alltTextWords;

	/**
	 * 先验概率
	 */
	public double prior() {
		return (double) (alltext.size())
				/ (double) LocalClass.getInstance().Examples.size();
	}

	/**
	 * 条件概率
	 * 
	 * @param wordName
	 *            单词名字
	 */
	public double condprob(String wordName) {
		double temp1 = (double) ((newDict.containsKey(wordName) ? newDict.get(
				wordName).getNumber() : 0) + 1);
		double temp2 = (double) (sumInOneClass + LocalClass.getInstance().sumWordsInAllFiles);
		return temp1 / temp2;
	}

	/**
	 * 单词在该类所有文档中出现的次数
	 */
	public HashMap<String, Word> nkHashMap;
	/**
	 * 经过开方检验之后，降维之后保存的结果
	 */
	public HashMap<String, Word> CHInkHashMap;
	/**
	 * 分值
	 */
	public double score;
	/**
	 * 经过开方检验和TFIDF之后的结果
	 */
	public HashMap<String, Word> newDict;

	public int sumInOneClass = 0;
	/**
	 * 每类的准确率
	 */
	public double Precision = 0.0d;
	/**
	 * 每类的召回率
	 */
	public double Recall = 0.0d;
}
