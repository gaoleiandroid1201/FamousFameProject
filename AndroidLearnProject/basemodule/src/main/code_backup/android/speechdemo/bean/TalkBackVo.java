package com.android.speechdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LiuXing on 2016/10/27.
 */
public class TalkBackVo implements Serializable {
    /**
     * sentence，第几句
     */
    private int sn;
    /**
     * last sentence，是否最后一句
     */
    private boolean ls;
    /**
     * begin，开始
     */
    private int bg;
    /**
     * end，结束
     */
    private int ed;
    /**
     * words，词
     */
    private List<TalkBackWordsVo> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<TalkBackWordsVo> getWs() {
        return ws;
    }

    public void setWs(List<TalkBackWordsVo> ws) {
        this.ws = ws;
    }
}
