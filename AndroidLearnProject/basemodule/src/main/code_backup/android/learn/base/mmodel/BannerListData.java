package com.android.learn.base.mmodel;

import java.util.List;


public class BannerListData extends BaseData {

//    {
//        "desc":"最新项目上线啦~", "id":13, "imagePath":
//        "http://www.wanandroid.com/blogimgs/5ae04af4-72b9-4696-81cb-1644cdcd2d29.jpg", "isVisible":
//        1, "order":0, "title":"最新项目上线啦~", "type":0, "url":"http://www.wanandroid.com/pindex"
//    }


    public List<BannerData> data;

    public class BannerData {

        public int id;
        public String url;
        public String imagePath;
        public String title;
        public String desc;
        public int isVisible;
        public int order;
        public int type;

    }


}