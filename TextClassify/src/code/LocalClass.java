package code;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LocalClass INSTANCE = null;

	private LocalClass() {
	}

	public static LocalClass getInstance() {
		return INSTANCE;
	}

	public static void setInstance(LocalClass temp) {
		INSTANCE = temp;
	}

	static {
		INSTANCE = new LocalClass();
	}

	public HashMap<String, Word> allNewDict = new HashMap<String, Word>();
	public List<TextClass> classes = new ArrayList<TextClass>();
	public List<Word> Vocabulary = new ArrayList<Word>();
	// public static List<String> vocabularyStrings = new ArrayList<String>();
	public List<File> Examples = new ArrayList<File>();
	public float featureNum = 0.99f;
	public int sumWordsInAllFiles = 0;
	public HashMap<String, HashMap<String, Integer>> normalTFAllFiles = new HashMap<String, HashMap<String, Integer>>();

}
