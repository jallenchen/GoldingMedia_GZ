package com.goldingmedia.mvp.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.mvp.mode.Category;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jallen on 2017/7/28 0028 17:35.
 */

public class MoviesShowAdapter extends BaseAdapter {
    private Context mContext;
    private List<Category> mCategories = new ArrayList<Category>();
    public MoviesShowAdapter(Context ct){
        mContext = ct;
    }
    public void refresh(List<Category> categories){
        if(categories == null || categories.size() == 0){
            return;
        }

        mCategories = categories;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mCategories.size() -1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Category category = mCategories.get(position+1);
        String imgPath = Contant.getTruckMetaNodePath(category.getCategoryId(),category.getCategorySubId(),"",true)+category.getCategorySubId()+".jpg";
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_moviesshow, parent,false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.moviesSub = (TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.moviesSub.setText(mCategories.get(position+1).getCategorySubDesc());

        holder.imageView.setImageBitmap( BitmapFactory.decodeFile(imgPath));
        return convertView;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView  moviesSub;
    }
}
