package com.goldingmedia.mvp.view.ui;

/**
 * Created by Jallen on 2018/6/4 0004 18:29.
 */

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by zlc on 2016/10/10.
 */

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(GDApplication.getmInstance()).load(path)
                .signature(new StringSignature(Contant.PushTime))
                .placeholder(R.color.transparent)
                .into(imageView);
    }
}
