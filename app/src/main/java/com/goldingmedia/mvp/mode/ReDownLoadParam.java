package com.goldingmedia.mvp.mode;

/**
 * Created by Jallen on 2017/10/13 0013 15:21.
 */

public class ReDownLoadParam{
    private String tabName;
    private int categoryId;
    private int categorySubId;
    private String fileName;
    private int vail;
    private String md5;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategorySubId() {
        return categorySubId;
    }

    public void setCategorySubId(int categorySubId) {
        this.categorySubId = categorySubId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getVail() {
        return vail;
    }

    public void setVail(int vail) {
        this.vail = vail;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
