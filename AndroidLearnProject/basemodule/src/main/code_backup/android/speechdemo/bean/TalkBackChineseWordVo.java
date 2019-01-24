package com.android.speechdemo.bean;

import java.io.Serializable;

/**
 * Created by LiuXing on 2016/10/27.
 */
public class TalkBackChineseWordVo implements Serializable {
    /**
     * word，单字
     */
    private String w;
    /**
     * score，分数
     */
    private int sc;

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }
}
