package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.utils.Utils;


import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment{

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //added by jallen
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void OnEventCmd(final  EventBusCMD cmd) {

        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                switch (cmd.getCmdId()){
                    case Contant.MsgID.REFLESH_DATA:
                        GDApplication.getmInstance().getTruckMedia().getcHotZone().setTruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_HOTZONE,1));
                        GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_MEDIA_ID));
                        GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmGOLDINGtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,1));
                        GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmDGTVtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,2));
                        GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmWhalestruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,3));
                        GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmSDLMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,4));
                        GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_GOLDING_ID));
                        GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmJTVtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,1));
                        GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmMagazinetruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,2));
                        GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmJMalltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,3));
                        GDApplication.getmInstance().getTruckMedia().getcGameCenter().setLMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,1));
                        GDApplication.getmInstance().getTruckMedia().getcGameCenter().setMMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,2));
                        GDApplication.getmInstance().getTruckMedia().getcGameCenter().setSMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,3));
                        GDApplication.getmInstance().getTruckMedia().getCeLive().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_ELIVE_ID));
                        GDApplication.getmInstance().getTruckMedia().getCeLive().setmMalltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,1));
                        GDApplication.getmInstance().getTruckMedia().getCeLive().setmHoteltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,2));
                        GDApplication.getmInstance().getTruckMedia().getCeLive().setmFoodtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,3));
                        GDApplication.getmInstance().getTruckMedia().getCeLive().setmTraveltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,4));
                        GDApplication.getmInstance().getTruckMedia().getcMyApp().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_MYAPP_ID));
                        GDApplication.getmInstance().getTruckMedia().getcMyApp().seteBooktruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,1));
                        GDApplication.getmInstance().getTruckMedia().getcMyApp().setApptruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,2));
                        GDApplication.getmInstance().getTruckMedia().getcMyApp().setSettingtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,3));
                        GDApplication.getmInstance().getTruckMedia().getcAds().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_ADS_ID));

                        EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_UI));
                        break;
                }
            }
        });
    }

}
