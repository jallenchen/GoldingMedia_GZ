package com.goldingmedia.mvp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jallen on 2017/7/28 0028 18:30.
 */

public class MyBaseGirdViewAdapter extends BaseAdapter {
    private Context mContext;
    List<TruckMediaProtos.CTruckMediaNode> mTrucks = new ArrayList<TruckMediaProtos.CTruckMediaNode>();

    public MyBaseGirdViewAdapter(Context ct){
        mContext = ct;
    }

    public void refresh(List<TruckMediaProtos.CTruckMediaNode> trucks){
        if(trucks == null || trucks.size() == 0){
            return;
        }
        mTrucks = trucks;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
//        if(mTrucks.size() == 0){
//            return mTrucks.size();
//        }else if(mTrucks.get(0).getCategoryId() == Contant.CATEGORY_MEDIA_ID && mTrucks.get(0).getCategorySubId() == Contant.PROPERTY_MEDIA_GOLDING_ID
//                && mTrucks.get(0).getMediaInfo().getTruckMeta().getTruckMediaType() ==  Contant.MEDIA_TYPE_MOVIE){
//            return mTrucks.size()+1;
//        }
        return mTrucks.size();
    }

    @Override
    public Object getItem(int position) {
        if (mTrucks == null) return null;
        else if (position >= mTrucks.size()) return null;
        return mTrucks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TruckMediaProtos.CTruckMediaNode truckMediaNode=null;
        int categoryId=0;
        int categorySubId=0;
        String imgPath="";
        String imgName="";
        String imgTitleName = "";

        if(mTrucks.size() == position){
            List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_MOVICEAREA);
            if(truckMediaNodes.size() == 0){

            }else{
                truckMediaNode = truckMediaNodes.get(0);//for test
                categoryId = truckMediaNode.getMediaInfo().getCategoryId();
                categorySubId = truckMediaNode.getMediaInfo().getCategorySubId();
                imgPath = Contant.getTruckMetaNodePath(categoryId,categorySubId,truckMediaNode.getMediaInfo().getTruckMeta().getTruckFilename(),true);
                imgName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckImage();
                imgTitleName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckTitle();
            }

        }else{
            truckMediaNode = mTrucks.get(position);
            categoryId = truckMediaNode.getMediaInfo().getCategoryId();
            categorySubId = truckMediaNode.getMediaInfo().getCategorySubId();
            imgPath = Contant.getTruckMetaNodePath(categoryId,categorySubId,truckMediaNode.getMediaInfo().getTruckMeta().getTruckFilename(),true);
            imgName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckImage();
            imgTitleName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckTitle();
        }

        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_base_gird, parent,false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.txtView = (TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
       // holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath+"/"+imgName));
        Glide.with(mContext).load(imgPath+"/"+imgName)
                .placeholder(R.color.transparent)
                .into(holder.imageView);
        //Glide.with(mContext).load(imgPath+"/"+imgName).placeholder(R.mipmap.base_grid_normal).into( holder.imageView);

        holder.txtView.setText(imgTitleName);
        if(categoryId == Contant.CATEGORY_MYAPP_ID && categorySubId == Contant.PROPERTY_MYAPP_SETTING_ID){
            holder.txtView.setVisibility(View.GONE);
        }else{
            holder.txtView.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView txtView;
    }
}
