package com.goldingmedia.temporary;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.mvp.mode.Category;
import com.goldingmedia.temporary.Modes.ListSubOrder;
import com.goldingmedia.temporary.Modes.PlayGroupMode;
import com.goldingmedia.temporary.Modes.PlayerConfigMode;

import java.util.ArrayList;
import java.util.List;

public class GetProtocolBuffer {
	public static List<Category> mCategory = new ArrayList<>();

	public static PlayerConfigMode getPlayerConfigMode() {
		mCategory = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getmCategorys();

		PlayerConfigMode mode = new PlayerConfigMode();
		mode.list_sub_order = ListSubOrder.NAME;
		mode.category_sub = true;
		mode.category_sub_order = getMoviceSubIds();
		mode.category_main_order = new int[]{6,7,1,2,3,4,5};
		return mode;
	}

	private static int[] getMoviceSubIds(){
		int[] subIds = new int[mCategory.size()];
		for(int i = 0;i<mCategory.size();i++){
			subIds[i] = mCategory.get(i).getCategorySubId();
		}
		return subIds;
	}

	private static String[] getMoviceSubDecs(){
		String[] subDecs = new String[mCategory.size()];
		for(int i = 0;i<mCategory.size();i++){
			subDecs[i] = mCategory.get(i).getCategorySubDesc();
		}
		return subDecs;
	}

	
	public static ArrayList<PlayGroupMode> getGroupList(PlayerConfigMode playerConfigMode) {
		
		ArrayList<PlayGroupMode> groupModeList = new ArrayList<PlayGroupMode>();
		int groupId = 0;
		int[] id;
		String[] order;
		if (playerConfigMode.category_sub) {
			id = playerConfigMode.category_sub_order;
			//order = category_sub_order;
			order = getMoviceSubDecs();
			for (int i = 0; i < id.length; i++) {
				PlayGroupMode mode = new PlayGroupMode();
				mode.groupId = groupId++;
				mode.classSubId = id[i];
				mode.classMainId = Contant.MEDIA_TYPE_MOVIE;
				mode.name = order[i];
				groupModeList.add(mode);
			}

			id = playerConfigMode.category_main_order;
			//order = category_main_order;
			order = GDApplication.getmInstance().getResources().getStringArray(R.array.mediatype_array);
			for (int i = 0; i < id.length; i++) {
				PlayGroupMode mode = new PlayGroupMode();
				mode.groupId = groupId++;
				mode.classSubId = -1;
				mode.classMainId = id[i];
				mode.name = order[mode.classMainId - 1];
				groupModeList.add(mode);
			}
		} else {
			id = playerConfigMode.category_main_order;
			//order = category_main_order;
			order = GDApplication.getmInstance().getResources().getStringArray(R.array.mediatype_array);
			for (int i = 0; i < id.length; i++) {
				PlayGroupMode mode = new PlayGroupMode();
				mode.groupId = groupId++;
				mode.classSubId = -1;
				mode.classMainId = id[i];
				mode.name = order[mode.classMainId - 1];
				groupModeList.add(mode);
			}

			id = playerConfigMode.category_sub_order;
			order = getMoviceSubDecs();
			for (int i = 0; i < id.length; i++) {
				PlayGroupMode mode = new PlayGroupMode();
				mode.groupId = groupId++;
				mode.classSubId = id[i];
				mode.classMainId = Contant.MEDIA_TYPE_MOVIE;
				mode.name = order[i];
				groupModeList.add(mode);
			}
		}
		return groupModeList;
	}
}