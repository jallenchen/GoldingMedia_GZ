package com.goldingmedia.mvp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.R;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.fragment.AboutUsFragment;
import com.goldingmedia.mvp.view.fragment.DisplayNumFragment;
import com.goldingmedia.mvp.view.fragment.EngModeFragment;
import com.goldingmedia.mvp.view.fragment.SettingLanguageFragment;
import com.goldingmedia.mvp.view.fragment.SystemInfoFragment;

public class SettingActivity extends BaseActivity {
    private TruckMediaProtos.CTruckMediaNode truck;
    private RelativeLayout passwd_lay;
    private EditText passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        passwd_lay = (RelativeLayout)findViewById(R.id.passwd_lay);
        passwd = (EditText)findViewById(R.id.passwd);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        truck = (TruckMediaProtos.CTruckMediaNode) intent.getSerializableExtra("truck");
        if(position == -1){
            initEngModeFragment();
        }else{
            passwd_lay.setVisibility(View.GONE);
            initFragment();
        }

    }

    private void initFragment(){
        switch (truck.getMediaInfo().getTruckMeta().getTruckDesc()) {
            case "DisplayNumFragment":
                initDisplayNumFragment();
                break;
            case "AboutUsFragment":
                initAboutFragment();
                break;
            case "SettingLanguageFragment":
                initlaungFragment();
                break;
            case "SystemInfoFragment":
                initSysInfoFragment();
                break;
            case "EngModeFragment":
                passwd_lay.setVisibility(View.VISIBLE);
                //initEngModeFragment();
                break;
            default:
                finish();
                break;
        }
    }

    private void initDisplayNumFragment(){
        DisplayNumFragment DisplayNum =  new DisplayNumFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_contaner, DisplayNum);
        ft.commit();
    }

    private void initAboutFragment(){
        AboutUsFragment aboutUsFragment =  new AboutUsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_contaner, aboutUsFragment);
        ft.commit();
    }

    private void initSysInfoFragment(){
        SystemInfoFragment systemInfo =  new SystemInfoFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_contaner, systemInfo);
        ft.commit();
    }

    private void initlaungFragment(){
        SettingLanguageFragment initlaung =  new SettingLanguageFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_contaner, initlaung);
        ft.commit();
    }

    private void initEngModeFragment(){
        EngModeFragment EngMode =  new EngModeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_contaner, EngMode);
        ft.commit();
    }

    public void onPasswdOK(View v){
        if(passwd.getText().toString().equals("828")){
            initEngModeFragment();
            passwd_lay.setVisibility(View.GONE);
        }else {
            passwd.setText("");
            EngModeFragment.Toast(getResources().getString(R.string.pwderr), this);
        }
    }

    public void onBack(View v){
        finish();
    }
}
