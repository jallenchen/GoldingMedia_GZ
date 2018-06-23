package com.goldingmedia.mvp.view.ui;

/**
 * Created by Jallen on 2018/6/4 0004 18:29.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;

/**
 * Created by zlc on 2016/10/10.
 */

public class ImageViewHolder implements Holder<String>{

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
       // Glide.with(context).load(data).placeholder(R.color.transparent).into(imageView);
        //imageView.setImageBitmap( BitmapFactory.decodeFile(data));
        Glide.with(context).load(data)
                .signature(new StringSignature(Contant.PushTime))
                .placeholder(R.color.transparent)
                .into(imageView);
    }
}
