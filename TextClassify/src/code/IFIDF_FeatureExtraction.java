package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Administrator
 * TF表示词条在文档d中出现的频率 ,分子是该词在文件中的出现次数，而分母则是在文件中所有字词的出现次数之和。
 * IDF由总文件数目除以包含该词语之文件的数目，再将得到的商取对数得到。|D|：语料库中的文件总数,包含该词语之文件的数目
 */
public class IFIDF_FeatureExtraction {

	//public static HashMap<String, HashMap<String, Float>>SortedIFIDFDict=new HashMap<String, HashMap<String,Float>>();
	public static HashMap<String, Float> SortedIFIDFDict=new HashMap<String, Float>();
	public static float IFIDFNum=0.7f;
	private static ArrayList<String> FileList = new ArrayList<String>();
	 
	public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException
    {
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        readDirs(filepath + "\\" + flist[i]);
                    }                    
                }
            }
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }
	//term frequency in a file, times for each word
	/**
	 * 
	 * @param fileDir 文件的路径
	 * @return 分词之后,进行取名词操作
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private static HashMap<String, Integer> cutWords(String fileDir) throws FileNotFoundException, IOException {
		// TODO 自动生成的方法存根
		String fileText = DataManager.readFile(fileDir);
    	HashMap<String, Integer> fileHashMap=new HashMap<String, Integer>();
    	String fileSegment = NlpirTest.chineseSegment(fileText);// 对文本进行分词
    	String pattern = "(?<=)[\\u4e00-\\u9fa5]+(/n/|/vn/)(\\d*[1-9]\\d*)(?=#)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(fileSegment);
		while (m.find()) {
//			System.out.println(m.group());
			String[] temp = m.group().split("/n/|/vn/");
			String key =temp[0];
			String number = temp[1];
//			if(!NlpirTest.isStopword(key))
//			{
				fileHashMap.put(key, Integer.parseInt(number));
//			}
		}
		return fileHashMap;
	}
	/**
	 * @param cutwords
	 * @return
	 */
//    public static HashMap<String, Integer> normalTF(HashMap<String, Integer> cutWords)
//    {
//    	return cutWords;
//    }
    
    //term frequency in a file, frequency of each word
    /**
     * 计算单词的tf值
     * @param cutwords
     * @return
     */
    public static HashMap<String, Float> tf(HashMap<String, Integer> cutWords){
        HashMap<String, Float> resTF = new HashMap<String, Float>();
        int wordLen=0;
        HashMap<String, Integer> intTF = cutWords;    
        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            wordLen+=Float.parseFloat(entry.getValue().toString());
        }
        Iterator iter1 = intTF.entrySet().iterator(); //iterator for that get from TF
        while(iter1.hasNext()){
            Map.Entry entry = (Map.Entry)iter1.next();
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
            System.out.println("TF："+entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTF;
    } 
    
   /* *//**
     * 
     * @param dirc
     * @return 
     * @throws IOException
     *//*
    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException{
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String,Integer>>();
        
        List<String> filelist = readDirs(dirc);
        for(String file : filelist){
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            HashMap<String, Integer> cutwords=cutWords(file); //get cut word for one file
            
            dict = normalTF(cutwords);
            allNormalTF.put(file, dict);
        }    
        return allNormalTF;
    }*/
    /**
     * tf for all file
     * @param dirc
     * @return
     * @throws IOException
     */
    public static HashMap<String,HashMap<String, Float>> tfAllFiles(String dirc) throws IOException{
        List<String> filelist = readDirs(dirc);
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        for(String file : filelist){
            HashMap<String, Float> dict = new HashMap<String, Float>();  
            HashMap<String, Integer> normaldict= cutWords(file); 
            LocalClass.getInstance().normalTFAllFiles.put(file, normaldict);
            dict = tf(normaldict);
            allTF.put(file, dict);
        }
        return allTF;
    }
    /**
     * 
     * @param all_tf
     * @return
     */
    public static HashMap<String, Float> idf(HashMap<String,HashMap<String, Float>> all_tf){
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                if(dict.get(word) == null){
                    dict.put(word, 1);
                }else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        System.out.println("IDF for every word is:");
        Iterator iter_dict = dict.entrySet().iterator();
        while(iter_dict.hasNext()){
            Map.Entry entry = (Map.Entry)iter_dict.next();
            float value = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
//            System.out.println(entry.getKey().toString() + " = " + value);
        }
        return resIdf;
    }
    /**
     * 
     * @param all_tf
     * @param idfs
     */
    public static void tf_idf(HashMap<String,HashMap<String, Float>> all_tf,HashMap<String, Float> idfs){
        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<String, HashMap<String, Float>>();
            
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++)
        {
            String filepath = FileList.get(i);
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word); 
                tfidf.put(word, value);
            }
            resTfIdf.put(filepath, tfidf);
        }
        System.out.println("TF-IDF for Every file is :");
        DisTfIdf(resTfIdf);
    }
    /**
     * 对每篇文档的每个单词按照TFIDF排序,并重新保存在SortedIFIDFDict中
     * @param tfidf
     */
    public static void DisTfIdf(HashMap<String, HashMap<String, Float>> tfidf)
    {
        Iterator iter1 = tfidf.entrySet().iterator();
        while(iter1.hasNext())
        {
            Map.Entry entrys = (Map.Entry)iter1.next();
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.println("TF-IDF for each word");
            /**
             *  对每篇里面的
             */
            Map<String, Float> tempHashMap=(HashMap<String, Float>) entrys.getValue();
			List<Map.Entry<String, Float>> infoIds = new ArrayList<Map.Entry<String, Float>>(tempHashMap.entrySet());
			
			//对每个文件中的单词按照TFIDF由大到小排序
			Collections.sort(infoIds, new Comparator<Map.Entry<String, Float>>() {   
			    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {      
			    	float result = o2.getValue() - o1.getValue();
			    	  if(result > 0)
			    		  return 1;
			    	  else if(result == 0)
			    	      return 0;
			    	  else 
			    	      return -1;
			    }
			}); 
/**
 * 
 */
            for(int i=0;i<IFIDFNum*infoIds.size();i++)
            {
            	HashMap<String, Float> tmpMap=new HashMap<String, Float>();
            	String word=infoIds.get(i).getKey();
            	float value=infoIds.get(i).getValue();
            	tmpMap.put(word, value);
            	System.out.println("TF-IDF"+word+"="+value+",");
            	//SortedIFIDFDict.put(entrys.getKey().toString(), tmpMap);
            	SortedIFIDFDict.putAll(tmpMap);
            } 
        }  
    }
    public static void IFIDF_Func(String file) throws IOException
    {
         HashMap<String,HashMap<String, Float>> all_tf = tfAllFiles(file);
         System.out.println();
         HashMap<String, Float> idfs = idf(all_tf);
         System.out.println();
         tf_idf(all_tf, idfs);
    }   
}
