package com.goldingmedia.temporary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.goldingmedia.R;

public class ImageProcessing {

	public static Bitmap ConvertBitMap(String path){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		int wRatio = (int)Math.ceil(op.outWidth/342);
		int hRatio = (int)Math.ceil(op.outHeight/200);
		if(wRatio > 1 && hRatio > 1){
			if(wRatio > hRatio){
				op.inSampleSize = wRatio;
			}else{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path,op);
	}
}