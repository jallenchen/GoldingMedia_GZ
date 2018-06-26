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
import com.goldingmedia.mvp.view.fragment.GameFragment;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jallen on 2017/7/28 0028 18:30.
 */

public class MyBaseGirdViewAdapter extends BaseAdapter {
    private Context mContext;
    List<TruckMediaProtos.CTruckMediaNode> mTrucks = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private boolean mHasAds = true;
    private final  int TYPE_COUNT_2 = 2;
    private final  int TYPE_COUNT_1 = 1;
    private final  int TYPE_BASE = 0;
    private final  int TYPE_ADS = 1;
    private List<String> mAdsImg;
    private OnBannerListener mListener;

    public MyBaseGirdViewAdapter(Context ct){
        mContext = ct;
    }

    public void refresh(List<TruckMediaProtos.CTruckMediaNode> trucks,List<String> ads){
        if(trucks == null || trucks.size() == 0){
            return;
        }
        mTrucks = trucks;
        mAdsImg = ads;
        if(mAdsImg == null || mAdsImg.size() == 0){
            mHasAds = false;
        }else{
            mHasAds = true;
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(mHasAds){
            return  mTrucks.size()+1;
        }
        return mTrucks.size();
    }

    @Override
    public Object getItem(int position) {
        if (mTrucks == null) return null;
        else if (position >= mTrucks.size()) return null;
        return mTrucks.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(mHasAds){
            if(position == mTrucks.size()){
                return TYPE_ADS;
            }
        }
        return TYPE_BASE;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT_2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setBannerListener( OnBannerListener listener){
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderAds holderAds = null;
        int type = getItemViewType(position);

        TruckMediaProtos.CTruckMediaNode truckMediaNode=null;
        int categoryId=0;
        int categorySubId=0;
        String imgPath="";
        String imgName="";
        String imgTitleName = "";

        if(type == TYPE_BASE && position < mTrucks.size()){
            truckMediaNode = mTrucks.get(position);
            categoryId = truckMediaNode.getMediaInfo().getCategoryId();
            categorySubId = truckMediaNode.getMediaInfo().getCategorySubId();
            imgPath = Contant.getTruckMetaNodePath(categoryId,categorySubId,truckMediaNode.getMediaInfo().getTruckMeta().getTruckFilename(),true);
            imgName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckImage();
            imgTitleName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckTitle();
        }
        if(convertView == null){
            switch (type){
                case TYPE_BASE:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_base_gird, parent,false);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                    holder.txtView = (TextView) convertView.findViewById(R.id.txt);
                    convertView.setTag(holder);
                    break;
                case TYPE_ADS:
                    holderAds = new ViewHolderAds();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_movies_ads, parent,false);
                    holderAds.banner = (Banner) convertView.findViewById(R.id.banner);
                    setBanner(holderAds.banner);
                    convertView.setTag(holderAds);
                    break;

            }
        }else{
            switch (type) {
                case TYPE_BASE:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_ADS:
                    holderAds = (ViewHolderAds) convertView.getTag();
                    break;
            }
        }

        switch (type){
            case TYPE_BASE:
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
                break;
            case TYPE_ADS:
                holderAds.banner.setImages(mAdsImg);
                holderAds.banner.setImageLoader(new GlideImageLoader());
                break;
        }
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView txtView;
    }
    class ViewHolderAds{
        Banner banner;
    }

    private void setBanner(Banner view){

        view.setDelayTime(GameFragment.GAME_LOOP_TIME);
        view.isAutoPlay(true);//自动轮播
        view.setViewPagerIsScroll(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        view.setImages(mAdsImg);
        view.setImageLoader(new GlideImageLoader());
        view.setOnBannerListener(mListener);
        if(mAdsImg.size() != 0){
            view.start();
        }else {
            view.setOnClickListener(null);
        }
    }
}
