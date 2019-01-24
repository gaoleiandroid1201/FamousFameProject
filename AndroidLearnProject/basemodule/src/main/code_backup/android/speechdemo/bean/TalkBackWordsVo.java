package com.android.speechdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LiuXing on 2016/10/27.
 */
public class TalkBackWordsVo implements Serializable {
    /**
     * begin，开始
     */
    private int bg;
    /**
     * chinese word，中文分词
     */
    private List<TalkBackChineseWordVo> cw;

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public List<TalkBackChineseWordVo> getCw() {
        return cw;
    }

    public void setCw(List<TalkBackChineseWordVo> cw) {
        this.cw = cw;
    }
}

