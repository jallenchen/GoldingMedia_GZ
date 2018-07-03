package com.goldingmedia.temporary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.goldingmedia.GDApplication;
import com.goldingmedia.activity.ELineActivity;
import com.goldingmedia.activity.MediaPlayActivity;
import com.goldingmedia.activity.PreviewPlayActivity;
import com.goldingmedia.activity.WebActivity;
import com.goldingmedia.activity.WindowAdsPlayActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.activity.JmagazineActivity;
import com.goldingmedia.mvp.view.activity.ReadyBookActivity;
import com.goldingmedia.mvp.view.activity.SettingActivity;
import com.goldingmedia.temporary.Variables.StatusItem;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

public class CardManager {
	private static CardManager mCardManager;
	public static CardManager getInstance() {
		if (mCardManager == null) {
			mCardManager = new CardManager();
		}
		return mCardManager;
	}
	
	public void action(int position, TruckMediaProtos.CTruckMediaNode truck, Context context,String  filename) {
		Intent intent;
		NLog.d("Cardmanager","---action:"+context.toString());
		try {
			switch (truck.getCategoryId()) {
            case Contant.CATEGORY_HOTZONE_ID:// 热门推荐类
    //			intent = new Intent(context, PreviewPlayActivity.class);
    //			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //			intent.putExtra(StatusItem.classMainId, truck.getMediaInfo().getTruckMeta().getTruckMediaType());
    //			startActivity(position, truck, context, intent);

                intent = new Intent(context, MediaPlayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(StatusItem.position, position);
                intent.putExtra(StatusItem.classId, truck.getMediaInfo().getCategoryId());
                intent.putExtra(StatusItem.classSubId, truck.getMediaInfo().getCategorySubId());
                intent.putExtra(StatusItem.classMainId, truck.getMediaInfo().getTruckMeta().getTruckMediaType());
                intent.putExtra("filename2pos", filename);
                context.startActivity(intent);

                break;

            case Contant.CATEGORY_MEDIA_ID:// 多媒体
                switch (truck.getMediaInfo().getTruckMeta().getTruckMediaType()) {
                case Contant.MEDIA_TYPE_MUSIC:// 音乐
                    intent = new Intent(context, MediaPlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(StatusItem.classMainId, truck.getMediaInfo().getTruckMeta().getTruckMediaType());
                    intent.putExtra("filename2pos", filename);
                    startActivity(position, truck, context, intent);
                    break;

                default:// 视频
                    intent = new Intent(context, PreviewPlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(StatusItem.classMainId, truck.getMediaInfo().getTruckMeta().getTruckMediaType());
                    intent.putExtra("filename2pos", filename);
                    startActivity(position, truck, context, intent);
                    break;
                }
                break;

            case Contant.CATEGORY_GOLDING_ID:// 喜粤传媒
                switch (truck.getCategorySubId()) {
                    case Contant.PROPERTY_GOLDING_JTV_ID:// 喜悦电视
                        intent = new Intent(context, MediaPlayActivity.class);
                        //intent = new Intent(context, WebActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("filename2pos", filename);
                        startActivity(position, truck, context, intent);
                        break;

                    case Contant.PROPERTY_GOLDING_MAGAZINE_ID:
                        intent = new Intent(context, JmagazineActivity.class);
                        startActivity(position+1, truck, context, intent);
                        break;

                    default:
                        break;
                }
                break;

            case Contant.CATEGORY_ELIVE_ID:// e-在线
                switch (truck.getCategorySubId()) {
                    case Contant.PROPERTY_ELIVE_MALL_ID://
                        intent = new Intent(context, ELineActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(position, truck, context, intent);
                        break;

                    default:
                        intent = new Intent(context, ELineActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(position, truck, context, intent);
                        break;
                }
                break;

            case Contant.CATEGORY_MYAPP_ID:// 我的应用
                switch (truck.getCategorySubId()) {
                    case Contant.PROPERTY_MYAPP_EBOOK_ID:// 电子书
                        intent = new Intent(context, ReadyBookActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ebookV",truck);
                        intent.putExtra("ebookB",bundle);
                        startActivity(position, truck, context, intent);
                        break;
                    case Contant.PROPERTY_MYAPP_APP_ID:// 应用
                        if(truck.getMediaInfo().getTruckMeta().getTruckDesc() == null || truck.getMediaInfo().getTruckMeta().getTruckDesc().equals("")){
                            return;
                        }
                        Utils.openApp(context,truck.getMediaInfo().getTruckMeta().getTruckDesc());
                        break;
                    case Contant.PROPERTY_MYAPP_SETTING_ID:// 设置
                        if(truck.getMediaInfo().getTruckMeta().getTruckDesc() == null || truck.getMediaInfo().getTruckMeta().getTruckDesc().equals("")){
                            return;
                        }
                        intent = new Intent(context, SettingActivity.class);
                        intent.putExtra("filename2pos", filename);
                        startActivity(position, truck, context, intent);
                        break;

                    default:
                        break;
                }

                break;
                case Contant.CATEGORY_ADS_ID:// 我的应用
                    int  action = truck.getMediaInfo().getAdsMeta().getTruckAdsAction();
                    if(action == Contant.ADS_ACTION_URL){
                        intent = new Intent(context, WebActivity.class);
                        intent.putExtra("online",true);
                        intent.putExtra("url",truck.getMediaInfo().getAdsMeta().getTruckAdsUrl());
                        startActivity(position, truck, context, intent);
                    }else if(action == Contant.ADS_ACTION_SUB){
                        NLog.e("CardManager","action category id:"+truck.getMediaInfo().getAdsMeta().getTruckCategoryId());
                        NLog.e("CardManager","action category sub id:"+truck.getMediaInfo().getAdsMeta().getTruckCategorySubId());
                        String tabName =  Contant.getTabNameByCategoryId(truck.getMediaInfo().getAdsMeta().getTruckCategoryId());
                        String actionFilename = truck.getMediaInfo().getAdsMeta().getTruckFileName();
                        int actionSubId = truck.getMediaInfo().getAdsMeta().getTruckCategorySubId();

                        TruckMediaProtos.CTruckMediaNode actionTruck = GDApplication.getmInstance().getDataInsert().getMediaMetaDataTrucks(tabName,actionSubId,actionFilename).get(0);
                        int index = actionTruck.getMediaInfo().getTruckMeta().getTruckIndex();
                        NLog.e("CardManager","action TruckIndex:"+index);
                        //index change to list pos
                        action(index,actionTruck,context,actionFilename);
                    }else if(action == Contant.ADS_ACTION_VIDEO){
                        intent = new Intent(context, WindowAdsPlayActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("position", 0);
                        intent.putExtra("orient", 0);
                        intent.putExtra("truck", truck);
                        context.startActivity(intent);
                    }
                    break;

            default:
                break;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startActivity(int position, TruckMediaProtos.CTruckMediaNode truck, Context context, Intent intent) {
//		Log.i("", "----"+mode.classId+"|"+mode.classSubId+"|"+mode.cardId);
		intent.putExtra(StatusItem.position, position);
		intent.putExtra(StatusItem.classId, truck.getCategoryId());
		intent.putExtra(StatusItem.classSubId, truck.getCategorySubId());
		intent.putExtra("truck", truck);
		context.startActivity(intent);
	}
}
