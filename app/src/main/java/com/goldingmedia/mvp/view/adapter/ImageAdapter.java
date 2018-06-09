package com.goldingmedia.mvp.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.goldingmedia.R;

public class ImageAdapter extends BaseAdapter {

	Context context;
	ArrayList<Bitmap> bitmaps;

	public ImageAdapter(Context context, ArrayList<Bitmap> bitmaps) {
		this.context = context;
		this.bitmaps = bitmaps;
	}

	public Context getContext(){
		return context;
	}
	
	public ArrayList<Bitmap> getBitmaps() {
		return bitmaps;
	}
	
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bitmaps.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bitmaps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent,false);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			holder.imageView.setScaleType(ScaleType.CENTER_CROP);
		}
		if (bitmaps.size() > 0) {
            Bitmap item = bitmaps.get(position % bitmaps.size());
			holder.imageView.setImageBitmap(item);
		}
		return convertView;
	}
	static class ViewHolder{
		ImageView imageView;
	}

}
