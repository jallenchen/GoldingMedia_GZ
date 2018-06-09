package com.goldingmedia.mvp.view.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.goldingmedia.R;
import com.goldingmedia.mvp.mode.Category;

import java.util.List;

/**
 * Created by Jallen on 2017/8/23 0023 09:36.
 */

public class MyRadioGroupView {
    private RadioButton[] mRadioButtons;
    public MyRadioGroupView(Context context, RadioGroup mRadioGroup, List<Category> categories, int checkid) throws Exception{
        RadioButton[] radioButtons = new RadioButton[categories.size()];
        mRadioGroup.removeAllViews();

        for(int i = 0;i<categories.size() ;i++){
            radioButtons[i] = new RadioButton(context);
            radioButtons[i].setText(categories.get(i).getCategorySubDesc());
            radioButtons[i].setTextSize(20);
            radioButtons[i].setButtonDrawable(R.color.transparent);
            radioButtons[i].setBackgroundResource(R.drawable.sub_btn_bg_selector);
            radioButtons[i].setGravity(Gravity.CENTER);
            mRadioGroup.addView(radioButtons[i]);
        }
        mRadioButtons = radioButtons;
        if(mRadioButtons.length -1 >= checkid){
            radioButtons[checkid].setChecked(true);
        }

    }

    public int getRadioButtonId(int index){

        return mRadioButtons[index].getId();
    }

    public  void setRadioButtonCheckedId(int id){
        mRadioButtons[id].setChecked(true);
    }

    public void setRadioButtonsTxt(String[] txts){
        for(int i = 0;i<txts.length ;i++){
            mRadioButtons[i].setText(txts[i]);
        }
    }

}
