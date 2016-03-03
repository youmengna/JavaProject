package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.rmi.CORBA.Util;

import code.NlpirTest.CLibrary;
import utils.FileOperator;

public class BayesClassify {


	/**
	 * 取TFIDF和CHI的交集，形成的新的词典
	 */
	public static void formNewDict() {
		for (int i = 0; i < LocalClass.getInstance().classes.size(); i++) {
			TextClass textClass = LocalClass.getInstance().classes.get(i);
			textClass.newDict = new HashMap<String, Word>();
			HashMap<String, Word> chinkHashMap = textClass.CHInkHashMap;
			HashMap<String, Float> ifidfHashMap2 = IFIDF_FeatureExtraction.SortedIFIDFDict;
			Iterator iter2 = ifidfHashMap2.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				String word1 = (String) entry.getKey();
				if (chinkHashMap.containsKey(word1)) {
					Word value = chinkHashMap.get(word1);
					textClass.newDict.put(word1, value);
					textClass.sumInOneClass += value.getNumber();
					LocalClass.getInstance().sumWordsInAllFiles += value.getNumber();
				}
			}
			System.out.println("最终词库"+textClass.className+"类的维数为"+textClass.newDict.size());
			LocalClass.getInstance().allNewDict.putAll(textClass.newDict);
		}
	}
	/**
	 * 朴素贝叶斯训练
	 * 
	 * @param filePath
	 *            根路径
	 * @param V
	 *            类别集合
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void Learn_Naive_Bayes_Text(String filePath, List<TextClass> V)
			throws FileNotFoundException, IOException {
		File files = new File(filePath); // 读取每类中的文件的路径
		String[] filesList = files.list();
		for (int i = 0; i < filesList.length; i++)// 循环遍历每一类
		{
			TextClass tempClass = new TextClass();
			tempClass.className = filesList[i];
			// 得到Vocabulary,nk
			HashMap<String, Word> temHashMap = new HashMap<String, Word>();
			String tempPath = files.getAbsolutePath() + "\\" + filesList[i];

			tempClass.nkHashMap = NlpirTest.getNouns1(tempPath);
			// 得到docs,P(vj)
			tempClass.alltext = Arrays.asList(new File(tempPath).listFiles());
			LocalClass.getInstance().Examples.addAll(tempClass.alltext);
			LocalClass.getInstance().classes.add(tempClass);
		}

	}

	/**
	 * 贝叶斯测试
	 * 
	 * @param filePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static String Classify_Naive_Bayes_Text(String filePath) throws FileNotFoundException, IOException {
		// /////////////////////
		/**
		 * 
		 */
		HashMap<String, Integer> wordsHashMap = new HashMap<String, Integer>();
		String fileText = DataManager.readFile(filePath);
		String fileSegment = NlpirTest.chineseSegment(fileText);// 对文本进行分词
		String pattern = "(?<=)[\\u4e00-\\u9fa5]+(/n/|/vn/)(\\d*[1-9]\\d*)(?=#)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(fileSegment);
		while (m.find()) {
			// System.out.println(m.group());
			String[] temp = m.group().split("/n/|/vn/");
			String key = null;
			String value = null;
			key = temp[0];
			value = temp[1];
			if (LocalClass.getInstance().allNewDict.containsKey(key)) {
				if (wordsHashMap.containsKey(key)) {
					wordsHashMap.put(key, Integer.parseInt(value) + wordsHashMap.get(key));
				} else {
					wordsHashMap.put(key, Integer.parseInt(value));
				}
			}
		}
		// //////////////////////////////
		for (int i = 0; i < LocalClass.getInstance().classes.size(); i++) {
			TextClass textClass = LocalClass.getInstance().classes.get(i);
			textClass.score = Math.log(textClass.prior());
			Iterator<Entry<String, Integer>> iterator = wordsHashMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> entry = iterator.next();
				textClass.score += Math.log(textClass.condprob(entry.getKey()));
			}
		}
		java.util.Collections.sort(LocalClass.getInstance().classes, new Comparator() {
			public int compare(final Object o1, final Object o2) {
				final TextClass m1 = (TextClass) o1;
				final TextClass m2 = (TextClass) o2;
				return (m2.score - m1.score) > 0 ? 1 : -1;
			}
		});
		System.out.println("分类结果为:" + LocalClass.getInstance().classes.get(0).className);
		return LocalClass.getInstance().classes.get(0).className;
	}

	public static void batchSort(String filePath) throws FileNotFoundException, IOException {
		File export=new File("D:\\mn\\export");
		if (export.exists()) {
			deleteFile(export);
		}
		for (int i = 0; i < LocalClass.getInstance().classes.size(); i++) {
			TextClass textClass = LocalClass.getInstance().classes.get(i);
			String classImportDir = "D:\\mn\\import" + "\\" + textClass.className;
			File file = new File(classImportDir);
			String[] importList = file.list();
			textClass.b = importList == null ? 0 : importList.length;
			System.out.println(textClass.className + "的b=" + (textClass.b));
		}
		List<String> readAllFiles = DataManager.readAllFiles(filePath);
		for (String string : readAllFiles) {
			File tempFile = new File(string);
			String[] category = string.split("\\\\");
			String TextCategory1 = category[category.length - 2];
			String className = Classify_Naive_Bayes_Text(string);
			if (TextCategory1.equals(className)) {
				for (int i = 0; i < LocalClass.getInstance().classes.size(); i++) {
					TextClass textClass = LocalClass.getInstance().classes.get(i);
					if (textClass.className.equals(className)) {
						textClass.a++;
						break;
					}
				}
			}
			File dir = new File("D:\\mn\\export\\" + className);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File newfile = new File(dir, tempFile.getName());
			if (newfile.exists()) {
				newfile = new File(newfile.getAbsolutePath() + System.currentTimeMillis()+".txt");
				tempFile.renameTo(newfile);
			} else {
				tempFile.renameTo(newfile);
			}

		}
	}
/**
 * 显示每个类别的准确率和召回率
 */
	public static void showInfo() {
		float sumprecise=0f; float sumrecall=0f;
		float maxprecise=0f; float maxrecall=0f;
		float minprecise=0f; float minrecall=0f;
		for (int i = 0; i < LocalClass.getInstance().classes.size(); i++) 
		{
			TextClass textClass = LocalClass.getInstance().classes.get(i);
			String classExportDir = "D:\\mn\\export" + "\\" + textClass.className;
			File file2 = new File(classExportDir);
			String[] exportList = file2.list();
			textClass.c = exportList == null ? 0 : exportList.length;
			if (textClass.b == 0) {
				// System.out.println("待分类的文件中不包含"+textClass.className+"类的文件");
			} else if (textClass.c == 0) {
				System.out.println("分好的文件中不包含" + textClass.className + "类的文件");
			} else {
				float recall= (textClass.a) / (textClass.b);
				float precise=(textClass.a) / (textClass.c);
				System.out.println(textClass.className + "的召回率=" + (textClass.a) / (textClass.b));
				System.out.println(textClass.className + "的准确率=" + (textClass.a) / (textClass.c));
				sumrecall+=recall;
				sumprecise+=precise;
			}
		}
//		System.out.println("最大准确率为"+maxprecise);
//		System.out.println("最小准确率为"+minprecise);
//		System.out.println("最大准确率为"+maxprecise);
//		System.out.println("最小准确率为"+minprecise);
		System.out.println("平均准确率为"+sumprecise/9);
		System.out.println("平均召回率为"+sumrecall/9);	
	}
	
	 //递归删除文件夹
    private static void deleteFile(File file) {
     if (file.exists()) {//判断文件是否存在
      if (file.isFile()) {//判断是否是文件
       file.delete();//删除文件 
      } else if (file.isDirectory()) {//否则如果它是一个目录
       File[] files = file.listFiles();//声明目录下所有的文件 files[];
       for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
        deleteFile(files[i]);//把每个文件用这个方法进行迭代
       }
       file.delete();//删除文件夹
      }
     } else {
      System.out.println("所删除的文件不存在");
     }
    }


//	public static void main(String[] args) throws IOException {
//
//		IFIDF_FeatureExtraction.IFIDF_Func("D:\\TextClassify_TEST");
//		Learn_Naive_Bayes_Text("D:\\TextClassify_TEST", null);
//		CHI.countWordCHI();
//		CHI.sortWordCHI();
//		formNewDict();
//		FileOperator.saveObject(LocalClass.getInstance(), "D:\\mn", "save");
//		LocalClass.setInstance((LocalClass) FileOperator.readObject("D:\\mn\\save"));
//		batchSort("D:\\mn\\import");
//		showInfo();
//		CLibrary.Instance.NLPIR_Exit();
//	}

}
