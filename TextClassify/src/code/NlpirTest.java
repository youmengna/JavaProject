package code;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.SystemParas;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NlpirTest {
	static {
		String argu = ".";
		// String system_charset = "GBK";//GBK----0
		String system_charset = "UTF-8";
		int charset_type = 1;
		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
		}
	}

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"./NLPIR",
				CLibrary.class);

		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);

		public String NLPIR_FileProcess(String sSrc, String result,
				int bPOSTagged);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public int NLPIR_AddUserWord(String sWord);

		public int NLPIR_DelUsrWord(String sWord);

		public String NLPIR_WordFreqStat(String cText);

		public String NLPIR_GetLastErrorMsg();

		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param text
	 *            输入文本内容
	 * @return
	 */
	public static String chineseSegment(String text) {
		String argu = ".";
		// String system_charset = "GBK";//GBK----0
		String system_charset = "UTF-8";
		int charset_type = 1;

//		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;
//
//		if (0 == init_flag) {
//			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
//			System.err.println("初始化失败！fail reason is " + nativeBytes);
//			return null;
//		}
		try {
			nativeBytes = CLibrary.Instance.NLPIR_WordFreqStat(text);
			System.out.println("分词结果为： " + nativeBytes);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return nativeBytes;
	}

	/**
	 * 判断改词是不是停用词
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isStopword(String word) {
		String filepath = "./stop_words_ch.txt";
		try {
			List<String> stopWords = DataManager.readStopWords(filepath);
			for (String stopword:stopWords)
			{
				if(word.equalsIgnoreCase(stopword))
					return true;
			}
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 得到一个hashmap，hashmap保存的是某一类中的关键词及其出现次数
	 * */
	public static HashMap<String, Word> getNouns1(String cjdir)
			throws FileNotFoundException, IOException {
		HashMap<String, Word> eachClassTF = new HashMap<String, Word>();
//		String pattern = "(?<=)[\\u4e00-\\u9fa5]+(/n/|/vn/)(\\d*[1-9]\\d*)(?=#)";
//		List<String> filesList = DataManager.readFiles(cjdir); // 读取该类的文件路径列表
//		for (String filePath : filesList) {
//			String fileText = DataManager.readFile(filePath);
//			String fileSegment = chineseSegment(fileText);// 对文本进行分词
//			Pattern r = Pattern.compile(pattern);
//			Matcher m = r.matcher(fileSegment);
//			while (m.find()) {
////				System.out.println(m.group());
//				String[] temp = m.group().split("/n/|/vn/");
//				String key = null;
//				String number = null;
//				key = temp[0];
//				number = temp[1];
////				LocalClass.getInstance()..vocabularysum += Integer.parseInt(number);
//				/**
//				 * normalTF统计每篇文章中每个单词词频
//				 */
//				// IFIDF_FeatureExtraction.normalTF(key,
//				// Integer.parseInt(number));
//				if (eachClassTF.containsKey(key)) {
//					int num1 = Integer.parseInt(number);
//					int num2 = (eachClassTF.get(key)).getNumber();
//					eachClassTF.get(key).setNumber(num1 + num2);
//				} else {
//					Word word = new Word(0, 0, 0, 0, 0.0d, 0, null, null);
//					// if (!LocalClass.getInstance()..Vocabulary.contains(word))
//					LocalClass.getInstance()..Vocabulary.add(word);
//					int num3 = Integer.parseInt(number);
//					word.setNumber(num3);
//					word.setText(key); // 单词的内容
//					word.setCategory(cjdir); // 单词的类别
//					eachClassTF.put(key, word);
//				}
//
//			}
//		}
		List<String> filesList = DataManager.readFiles(cjdir); // 读取该类的文件路径列表
		for(String file:filesList)
		{
			HashMap<String,Integer> hashMap = LocalClass.getInstance().normalTFAllFiles.get(file);
			Iterator<Entry<String, Integer>> iterator = hashMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> next = iterator.next();
				String key=next.getKey();
				Integer number=next.getValue();
				if (eachClassTF.containsKey(key)) {
					int num1 = number;
					int num2 = (eachClassTF.get(key)).getNumber();
					eachClassTF.get(key).setNumber(num1 + num2);
				} else {
					Word word = new Word(0, 0, 0, 0, 0.0d, 0, null, null);
					// if (!LocalClass.getInstance()..Vocabulary.contains(word))
					LocalClass.getInstance().Vocabulary.add(word);
					int num3 = number;
					word.setNumber(num3);
					word.setText(key); // 单词的内容
					word.setCategory(cjdir); // 单词的类别
					eachClassTF.put(key, word);
				}
			}
		}
		return eachClassTF;
	}
}
