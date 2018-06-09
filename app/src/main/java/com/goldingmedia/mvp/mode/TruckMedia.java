package com.goldingmedia.mvp.mode;

/**
 * Created by Jallen on 2017/8/23 0023 17:13.
 */

public class TruckMedia {
    private CGoldingMedia cGoldingMedia = new CGoldingMedia();
    private CHotZone cHotZone = new CHotZone();
    private CGameCenter cGameCenter = new CGameCenter();
    private CELive ceLive = new CELive();
    private CMediaTypeMeta cMediaTypeMeta = new CMediaTypeMeta();
    private CMoviesShow cMoviesShow = new CMoviesShow();
    private CMyApp cMyApp = new CMyApp();
    private CAds cAds = new CAds();

    public CGoldingMedia getcGoldingMedia() {
        return cGoldingMedia;
    }

    public void setcGoldingMedia(CGoldingMedia cGoldingMedia) {
        this.cGoldingMedia = cGoldingMedia;
    }

    public CHotZone getcHotZone() {
        return cHotZone;
    }

    public void setcHotZone(CHotZone cHotZone) {
        this.cHotZone = cHotZone;
    }

    public CGameCenter getcGameCenter() {
        return cGameCenter;
    }

    public void setcGameCenter(CGameCenter cGameCenter) {
        this.cGameCenter = cGameCenter;
    }

    public CELive getCeLive() {
        return ceLive;
    }

    public void setCeLive(CELive ceLive) {
        this.ceLive = ceLive;
    }

    public CMediaTypeMeta getcMediaTypeMeta() {
        return cMediaTypeMeta;
    }

    public CMoviesShow getcMoviesShow() {
        return cMoviesShow;
    }

    public void setcMoviesShow(CMoviesShow cMoviesShow) {
        this.cMoviesShow = cMoviesShow;
    }

    public CMyApp getcMyApp() {
        return cMyApp;
    }

    public void setcMyApp(CMyApp cMyApp) {
        this.cMyApp = cMyApp;
    }

    public CAds getcAds() {
        return cAds;
    }

    public void setcAds(CAds cAds) {
        this.cAds = cAds;
    }


}
