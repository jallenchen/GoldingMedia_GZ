package com.goldingmedia.mvp.view.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Jallen on 2017/11/13 0013 10:43.
 */

public class MTextView extends TextView {

    public MTextView(Context context) {
        super(context);
    }

    public MTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
