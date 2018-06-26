package com.goldingmedia.mvp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goldingmedia.R;
import com.goldingmedia.mvp.view.fragment.GameFragment;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jallen on 2017/7/29 0029 13:47.
 */

public class GameGirdAdapter extends BaseAdapter {
    private Context mContext;
    private final  int TYPE_COUNT = 2;
    private final  int TYPE_BASE = 0;
    private final  int TYPE_ADS = 1;
    private List<String> mGameImgs = new ArrayList<>();
    private List<String> mAdsImgs = new ArrayList<>();
    private OnBannerListener mListener;

    public GameGirdAdapter(Context ct){
        mContext = ct;
    }

    public void refresh(List<String> imgs,List<String> imgAds){
        if (mGameImgs == null){
            return;
        }
        mGameImgs = imgs;
        mAdsImgs = imgAds;
        notifyDataSetChanged();
    }

    public void setBannerListener(OnBannerListener listener){
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 5){
            return TYPE_ADS;
        }else{
            return TYPE_BASE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return mGameImgs.size()+1;
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
        ViewHolderAds holderAds = null;
        int type = getItemViewType(position);

        if(convertView == null){
            switch (type){
                case TYPE_BASE:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_game, parent,false);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                    convertView.setTag(holder);
                    break;
                case TYPE_ADS:
                    holderAds = new ViewHolderAds();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_game_ads, parent,false);
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
                Glide.with(mContext).load(mGameImgs.get(position))
                        .placeholder(R.color.transparent)
                        .into(holder.imageView);
                break;
            case TYPE_ADS:
                holderAds.banner.setImages(mAdsImgs);
                holderAds.banner.setImageLoader(new GlideImageLoader());
                break;
        }
        //holder.imageView.setImageBitmap( BitmapFactory.decodeFile(imgPath+"/"+imgName));

        //Glide.with(mContext).load(imgPath+"/"+imgName).placeholder(R.mipmap.game_m_bg_normal).into( holder.imageView);

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
    }
    class ViewHolderAds{
        Banner banner;
    }

    private void setBanner(Banner view){

        view.setDelayTime(GameFragment.GAME_LOOP_TIME);
        view.isAutoPlay(true);//自动轮播
        view.setViewPagerIsScroll(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        view.setImages(mAdsImgs);
        view.setImageLoader(new GlideImageLoader());
        view.setOnBannerListener(mListener);
        if(mAdsImgs.size() != 0){
            view.start();
        }else {
            view.setOnClickListener(null);
        }
    }
}
