package com.goldingmedia.mvp.view.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LrcProcess {

	private List<LrcContent> LrcList;

	private LrcContent mLrcContent;

	public LrcProcess() {

		mLrcContent = new LrcContent();
		LrcList = new ArrayList<LrcContent>();
	}

	public String readLRC(String lrcpath) {
		LrcList.clear();
		StringBuilder stringBuilder = new StringBuilder();
		File f = new File(lrcpath);
		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "GB2312");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				s = s.replace("[", "");
				s = s.replace("]", "@");
				String splitLrc_data[] = s.split("@");
				if (splitLrc_data.length > 1) {
					mLrcContent.setLrc(splitLrc_data[1]);
					int LrcTime = TimeStr(splitLrc_data[0]);
					mLrcContent.setLrc_time(LrcTime);
					LrcList.add(mLrcContent);
					mLrcContent = new LrcContent();
				}
			}
			br.close();
			isr.close();
			fis.close();
		} catch(Exception e){
			//e.printStackTrace();
		}	
		
		return stringBuilder.toString();
	}

	/**
	 * 瑙ｆ瀽姝屾洸鏃堕棿澶勭悊绫�
	 */
	public int TimeStr(String timeStr) {

		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");
		String timeData[] = timeStr.split("@");
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}

	public List<LrcContent> getLrcContent() {
		return LrcList;
	}

	/**
	 * 鑾峰緱姝岃瘝鍜屾椂闂村苟杩斿洖鐨勭被
	 */
	public class LrcContent {
		private String Lrc;
		private int Lrc_time;

		public String getLrc() {
			return Lrc;
		}

		public void setLrc(String lrc) {
			Lrc = lrc;
		}

		public int getLrc_time() {
			return Lrc_time;
		}

		public void setLrc_time(int lrc_time) {
			Lrc_time = lrc_time;
		}
	}

}
