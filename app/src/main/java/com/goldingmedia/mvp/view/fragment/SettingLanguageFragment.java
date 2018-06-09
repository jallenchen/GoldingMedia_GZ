package com.goldingmedia.mvp.view.fragment;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.goldingmedia.LauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.sqlite.DataSharePreference;
import com.goldingmedia.utils.NLog;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingLanguageFragment extends BaseFragment implements View.OnClickListener{
    private FrameLayout cnFl;
    private FrameLayout twFl;
    private FrameLayout enFl;

    public SettingLanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_language, container, false);

        cnFl = (FrameLayout) view.findViewById(R.id.lang_cn_fl);
        twFl = (FrameLayout) view.findViewById(R.id.lang_tw_fl);
        enFl = (FrameLayout) view.findViewById(R.id.lang_en_fl);
        cnFl.setOnClickListener(this);
        twFl.setOnClickListener(this);
        enFl.setOnClickListener(this);
        initView();
        return view;
    }

    private void initView(){
       // String able= Locale.getDefault().toString();
        String local = DataSharePreference.getLanguage(getActivity(),"local");
        NLog.e("SettingLanguageFragment",local);
        switch (local){
            case "CN":
                cnFl.setSelected(true);
                twFl.setSelected(false);
                enFl.setSelected(false);
                break;
            case "TW":
                cnFl.setSelected(false);
                twFl.setSelected(true);
                enFl.setSelected(false);
                break;
            case "EN":
                cnFl.setSelected(false);
                twFl.setSelected(false);
                enFl.setSelected(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
       // Intent intent = new Intent("com.goldingmedia.language.settings");
        switch (v.getId()){
            case R.id.lang_cn_fl:
                cnFl.setSelected(true);
                twFl.setSelected(false);
                enFl.setSelected(false);
                setLanguage("CN");
                break;
            case R.id.lang_tw_fl:
                cnFl.setSelected(false);
                twFl.setSelected(true);
                enFl.setSelected(false);
                setLanguage("TW");
                break;
            case R.id.lang_en_fl:
                cnFl.setSelected(false);
                twFl.setSelected(false);
                enFl.setSelected(true);
               setLanguage("EN");
                break;
        }
    }

    private void setLanguage(String local){
      //  String able= getResources().getConfiguration().locale.getCountry();
        if(local.equals("EN") ){
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.US;
            getResources().updateConfiguration(config, dm);
        }else if(local.equals("TW") ){
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.TAIWAN;
            getResources().updateConfiguration(config, dm);
        }else{
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.SIMPLIFIED_CHINESE;
            getResources().updateConfiguration(config, dm);
        }
        DataSharePreference.saveLanguage(getActivity(),local);
        Intent intent = new Intent(getActivity(), LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
