package com.goldingmedia.temporary;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Txt {
	
    public static String[] GetFileContent( String filename){
    	String mStr = null;
    	try{
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			mStr = br.readLine();
    	    mStr = mStr.replace(".:..:", "");
    	    br.close();
    	    fr.close();
    	} catch (Exception e) {
			e.printStackTrace();
		}    
    	
    	if(TextUtils.isEmpty(mStr)){
    		return null;
    	} else {
    		return mStr.split(":");
    	}
    }
	 
	public static String getStrings(String txtPathString) {
		String strings = "";
		File f = new File(txtPathString);
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(txtPathString);
				BufferedReader br = new BufferedReader(fr);
				strings = br.readLine();
				br.close();
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return strings;
		} else {
			return "";
		}
	}

	public static void saveStringToTextFile(String str, String path) {
		File f = new File(path);
		try {
			if (f.exists()) {
				f.createNewFile();
			}
			FileWriter command = new FileWriter(f);
			{
				command.write(str);
				command.write("\n");
			}
			command.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
     
	public static void saveArrayToTextFile(String fileName, String[] array) {
		FileWriter writer = null;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			writer = new FileWriter(fileName, true);
			for (int i = 0; i < array.length; i++) {
	    		writer.write(array[i]);
	    		writer.write("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	    	try {
	        	if(writer != null){
	            	writer.close();
	        	}
	    	} catch (IOException e) {
	       	 	e.printStackTrace();
	    	}
	   	}
	}

	public static String getStringFromTextFile(String fileName) {
 		String ret = "";
 		File f = new File(fileName);
 		if (f.exists()) {
			try {
	 			FileReader fr;
				fr = new FileReader(fileName);
	 			BufferedReader br = new BufferedReader(fr);

	 			String str ;
	 			if ((str = br.readLine()) != null && !"".equals(str.trim())) {
	 				ret = str;
				}
	 			while((str = br.readLine()) != null && !"".equals(str.trim())) {
	 				ret = ret + "###" + str;
	 			}
	 			br.close();
	 			fr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 		}
 		return ret;
 	}
}