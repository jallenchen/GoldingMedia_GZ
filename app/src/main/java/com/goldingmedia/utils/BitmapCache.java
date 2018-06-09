package com.goldingmedia.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class BitmapCache {
    private final  LruCache<String, Bitmap> mCache;
    public BitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize =maxMemory/8;
        NLog.d("BitmapCache","maxMemory:"+maxMemory+" maxSize:"+maxSize);
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public Bitmap getBitmap(String path) {
    	if(path == null){
    		return null;
    	}
        Bitmap bitmap = mCache.get(path);
        if(bitmap == null){
            bitmap = BitmapFactory.decodeFile(path);
            putBitmap(path,bitmap);
        }

        return bitmap;
    }

    private void putBitmap(String path, Bitmap bitmap) {
      if(bitmap == null){
          return;
      }
        mCache.put(path, bitmap);
    }
}
