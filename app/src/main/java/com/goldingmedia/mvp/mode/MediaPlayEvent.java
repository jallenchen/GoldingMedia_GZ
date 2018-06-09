package com.goldingmedia.mvp.mode;

/**
 * Created by Jallen on 2017/7/20 0020 10:59.
 */

public class MediaPlayEvent {
    private int  mediaType;
    private int mediaId;
    private int listId;

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }
}
