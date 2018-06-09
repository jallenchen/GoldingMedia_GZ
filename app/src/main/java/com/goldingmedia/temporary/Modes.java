package com.goldingmedia.temporary;

public class Modes {

	public enum ListSubOrder {
		TIME, //按照媒体上线 时间 排序
		NAME, //按照媒体 名称 排序
		LIST //按照媒体 子类卡片列表 排序
	}
	public static class PlayerConfigMode {
		public ListSubOrder list_sub_order; // 播放媒体列表的顺序
		public boolean category_sub;        // 播放类型列表的顺序false: 先按 category_sub_id 排序; true: 先按 主界面 "电影 综艺 动漫 体育 戏剧 MTV 音乐" 排序
		public int[] category_sub_order;   // category_sub_order="2,3,1" ; 1金鼎传媒, 2广东卫视, 3鲸鱼海娱乐
		public int[] category_main_order;  // category_main_order="6,7,1,2,3,4,5" ;
		// 1电影, 2综艺, 3动漫, 4体育, 5戏剧, 6MTV, 7音乐
	}

	public static class PlayGroupMode {
		public int groupId;
		public int classSubId;
		public int classMainId;
		public String name;
	}

	public static class Order {
		public String ordersn;
		public int classId;
		public int classSubId;
		public String cardUuid;
		public long count;
		public long time;
		public String status;
		public String qrcodeUrl;
	}
}