package com.goldingmedia.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



public class StreamTool {
	public static final int MSG_PROGRESS = 5;
	private static ArrayList<String> EbookData = new ArrayList<String>();
	private static String EbookTxt = "";
	public static void saveData(InputStream in,File file) throws IOException{
		if(in !=null && file!=null){
			FileOutputStream out = new FileOutputStream(file);
			readData(in, out);
		}
	}
	
	public static byte[] read(InputStream in) throws IOException{
		byte[] bytes = null;
		if(in!=null){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			readData(in, out);
			bytes = out.toByteArray();
		}
		return bytes;
	}

	private static void readData(InputStream in,OutputStream out) throws IOException{
		if(in!=null && out!=null){
			BufferedInputStream bis = new BufferedInputStream(in);
			BufferedOutputStream bos = new BufferedOutputStream(out);
			int len = -1;
			byte[] bytes = new byte[1024];
			while((len = bis.read(bytes))!=-1){
				bos.write(bytes,0,len);
			}
			bos.close();
			out.close();
			bis.close();
			in.close();
		}
	}
	
	
	public static void save(File fromFile,File toFile) throws IOException{
		InputStream fis = new FileInputStream(fromFile);
		BufferedInputStream in = new BufferedInputStream(fis);
		OutputStream fos = new FileOutputStream(toFile);
		BufferedOutputStream out = new BufferedOutputStream(fos);
		
		int len = -1;
		byte[] bytes = new byte[1024];
		while((len = in.read(bytes))!=-1){
			out.write(bytes,0,len);
		}
		out.close();
		fos.close();
		in.close();
		fis.close();
	}
	
	public static ArrayList<String> readBook(InputStream in)throws IOException{
		 ArrayList<String> texts = new ArrayList<String>();
		 byte[] bytes = new byte[1024];
		 int len = -1;
		 while((len = in.read(bytes))!=-1){
			// String text = new String(bytes,"GB2312");
			 String text = new String(bytes,"UTF-8");
			 texts.add(text);
		 }
		 in.close();
		setEbookData(texts);
		return texts;
	}

	public static String readBookString(InputStream in)throws IOException{
		StringBuffer appendSB = new StringBuffer();
		byte[] bytes = new byte[in.available()];
		int len = -1;
		while((len = in.read(bytes))!=-1){
			String text = new String(bytes,"GB2312");
			appendSB.append(text);
		}
		in.close();
		setEbookString(appendSB.toString());
		return appendSB.toString();
	}

	private static void setEbookString(String txt){
		EbookTxt = txt;
	}

	public static String getEbookString(){
		return EbookTxt;
	}

	private static void setEbookData(ArrayList<String> txts){
		EbookData = txts;
	}

	public static ArrayList<String>  getEbookData(){
		return EbookData;
	}
}
