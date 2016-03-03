package code;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataManager {
	/**
     * @param args
     */    
    private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
    /**
     * @return 返回所有类别的所有文件列表
     * @param filepath 给定文件路径
     */
    //get list of file for the directory, including sub-directory of it
    public static List<String> readAllFiles(String filepath) throws FileNotFoundException, IOException
    {
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
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
                    	readAllFiles(filepath + "\\" + flist[i]);
                    }                    
                }
            }
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }
    /**
     * @return 返回所该类的所有文件列表
     * @param filepath 给定文件路径
     */
    //get list of file for the directory, including sub-directory of it
    public static List<String> readFiles(String filepath) throws FileNotFoundException, IOException
    {
        ArrayList<String> mFileList = new ArrayList<String>(); // the list of file

        File file = new File(filepath);
		if(!file.isDirectory())
		{
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
		        	mFileList.add(newfile.getAbsolutePath());
		        }                   
		    }
		}
        return mFileList;
    }
	/**
	* 返回给定路径的文本文件内容
	* @param filePath 给定的文本文件路径
	* @return 文本内容
	* @throws java.io.FileNotFoundException
	* @throws java.io.IOException
	*/
	public static String readFile(String filePath) throws FileNotFoundException,IOException 
	{
	
		InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"GB2312");
		BufferedReader reader = new BufferedReader(isReader);
		String aline;
		StringBuilder sb = new StringBuilder();
	
		while ((aline = reader.readLine()) != null)
		{
			sb.append(aline).append("\r\n");
		}
		isReader.close();
		reader.close();
		return sb.toString();
	}
	public static List<String> readStopWords(String filePath) throws FileNotFoundException,IOException 
	{
		InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"GB2312");
		BufferedReader reader = new BufferedReader(isReader);
		String aline;
		List<String> stopWordsList=new ArrayList<String>();
		while ((aline = reader.readLine()) != null)
		{
			stopWordsList.add(aline);
		}
		isReader.close();
		reader.close();
		return stopWordsList;
	}
}
