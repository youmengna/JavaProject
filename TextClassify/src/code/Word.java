package code;

import java.io.Serializable;
import java.util.List;

public class Word implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private int a;
	private int b;
	private int c;
	private int d;
	private int number;  //词频
	private String text; //单词内容
	private String category; //单词类别
	public double wordCHI;  // 单词的开放检验值
	public Word(int a, int b, int c, int d, Double wordCHI,int number, String text,String category) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.wordCHI=wordCHI;
		this.number = number;
		this.text = text;
		this.category = category;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}
}
