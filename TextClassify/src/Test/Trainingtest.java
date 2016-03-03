package Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.swing.JApplet;

import code.BayesClassify;
import code.CHI;
import code.IFIDF_FeatureExtraction;
import code.LocalClass;
import code.TextClass;
import code.NlpirTest.CLibrary;
import utils.FileOperator;

/**
 * 
 * 对训练集进行训练
 * @author  
 *
 */
public class Trainingtest {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		IFIDF_FeatureExtraction.IFIDF_Func("D:\\mn\\train");
		BayesClassify.Learn_Naive_Bayes_Text("D:\\mn\\train", null);
		CHI.countWordCHI();
		CHI.sortWordCHI();
		BayesClassify.formNewDict();
		File file = new File("D:\\mn", "save");
		if (file.exists()) {
			file.delete();
		}
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileOperator.saveObject(LocalClass.getInstance(), "D:\\mn", "save");
		CLibrary.Instance.NLPIR_Exit();
		System.out.println("最后训练样本集为"+LocalClass.getInstance().allNewDict.size()+"维");
		System.out.println("当前时间"+df.format(new java.util.Date()));
		System.out.println("训练完成！！");
	}

}
