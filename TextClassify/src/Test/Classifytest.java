package Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import code.BayesClassify;
import code.LocalClass;
import code.NlpirTest.CLibrary;
import utils.FileOperator;

/**
 * 对测试集进行测试分类，并显示分类结果
 * @author Administrator
 *
 */
public class Classifytest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		LocalClass.setInstance((LocalClass) FileOperator.readObject("D:\\mn\\save"));
		BayesClassify.batchSort("D:\\mn\\import");
		BayesClassify.showInfo();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println("当前时间"+df.format(new java.util.Date()));
		CLibrary.Instance.NLPIR_Exit();
	}

}
