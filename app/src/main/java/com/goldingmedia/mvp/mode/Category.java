package com.goldingmedia.mvp.mode;


/**
 * Created by Jallen on 2017/8/23 0023 17:16.
 */

public class Category {
    private int mCategoryId;
    private int mCategorySubId;
    private int mCategorySubIndex;
    private String mCategorySubDesc;

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public int getCategorySubId() {
        return mCategorySubId;
    }

    public void setCategorySubId(int mCategorySubId) {
        this.mCategorySubId = mCategorySubId;
    }

    public int getCategorySubIndex() {
        return mCategorySubIndex;
    }

    public void setCategorySubIndex(int mCategorySubIndex) {
        this.mCategorySubIndex = mCategorySubIndex;
    }

    public String getCategorySubDesc() {
        return mCategorySubDesc;
    }

    public void setCategorySubDesc(String mCategorySubDesc) {
        this.mCategorySubDesc = mCategorySubDesc;
    }
}
