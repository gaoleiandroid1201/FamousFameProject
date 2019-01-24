package com.android.learn.base.mmodel;

import java.io.Serializable;
import java.util.List;


/**
 * @author quchao
 * @date 2018/2/24
 */

public class ProjectListData {

    /**
     * "curPage": 1,
     * "datas": [],
     * "offset": 0,
     * "over": true,
     * "pageCount": 1,
     * "size": 15,
     * "total": 8
     */
    public ListData data;

    public class ListData {
        public int curPage;
        public List<ProjectData> datas;
        public int offset;
        public boolean over;
        public int pageCount;
        public int size;
        public int total;
        public List<ProjectData> getDatas() {
            return datas;
        }

        public void setDatas(List<ProjectData> datas) {
            this.datas = datas;
        }
//        public int getCurPage() {
//            return curPage;
//        }
//
//        public void setCurPage(int curPage) {
//            this.curPage = curPage;
//        }
//
//        public int getOffset() {
//            return offset;
//        }
//
//        public void setOffset(int offset) {
//            this.offset = offset;
//        }
//
//        public boolean isOver() {
//            return over;
//        }
//
//        public void setOver(boolean over) {
//            this.over = over;
//        }
//
//        public int getPageCount() {
//            return pageCount;
//        }
//
//        public void setPageCount(int pageCount) {
//            this.pageCount = pageCount;
//        }
//
//        public int getSize() {
//            return size;
//        }
//
//        public void setSize(int size) {
//            this.size = size;
//        }
//
//        public int getTotal() {
//            return total;
//        }
//
//        public void setTotal(int total) {
//            this.total = total;
//        }
    }

    public class ProjectData implements Serializable
    {

//            {
//    "apkLink": "",
//        "author": "LiangLuDev",
//        "chapterId": 294,
//        "chapterName": "完整项目",
//        "collect": false,
//        "courseId": 13,
//        "desc": "注册登录、用户信息、用户密码、用户图像修改、书籍分类、本地书籍扫描、书架、书籍搜索（作者名或书籍名）、书籍阅读（仅txt格式，暂不支持PDF等其他格式）、阅读字体、背景颜色、翻页效果等设置、意见反馈（反馈信息发送到我的邮箱）、应用版本更新",
//        "envelopePic": "http://www.wanandroid.com/blogimgs/fab6fb8b-c3aa-495f-b6a9-c007d78751c0.gif",
//        "fresh": false,
//        "id": 2836,
//        "link": "http://www.wanandroid.com/blog/show/2116",
//        "niceDate": "2018-04-22",
//        "origin": "",
//        "projectLink": "https://github.com/LiangLuDev/WeYueReader",
//        "publishTime": 1524376619000,
//        "superChapterId": 0,
//        "superChapterName": "",
//        "tags": [ ],
//        "title": "微Yue电子书阅读 WeYueReader",
//        "type": 0,
//        "visible": 1,
//        "zan": 0
//}
        public String apkLink;
        public String author;
        public int chapterId;
        public String chapterName;
        public boolean collect;
        public int courseId;
        public String desc;
        public String envelopePic;
        public int id;
        public String link;
        public String niceDate;
        public String origin;
        public String projectLink;
        public int superChapterId;
        public String superChapterName;
        public long publishTime;
        public String title;
        public int visible;
        public int zan;

//        public String getApkLink() {
//            return apkLink;
//        }
//
//        public void setApkLink(String apkLink) {
//            this.apkLink = apkLink;
//        }
//
        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getChapterId() {
            return chapterId;
        }
//
//        public void setChapterId(int chapterId) {
//            this.chapterId = chapterId;
//        }
//
//        public String getChapterName() {
//            return chapterName;
//        }
//
//        public void setChapterName(String chapterName) {
//            this.chapterName = chapterName;
//        }
//
//        public boolean isCollect() {
//            return collect;
//        }
//
//        public void setCollect(boolean collect) {
//            this.collect = collect;
//        }
//
//        public int getCourseId() {
//            return courseId;
//        }
//
//        public void setCourseId(int courseId) {
//            this.courseId = courseId;
//        }
//
        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getEnvelopePic() {
            return envelopePic;
        }

        public void setEnvelopePic(String envelopePic) {
            this.envelopePic = envelopePic;
        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getNiceDate() {
            return niceDate;
        }

        public void setNiceDate(String niceDate) {
            this.niceDate = niceDate;
        }
//
//        public String getOrigin() {
//            return origin;
//        }
//
//        public void setOrigin(String origin) {
//            this.origin = origin;
//        }
//
//        public String getProjectLink() {
//            return projectLink;
//        }
//
//        public void setProjectLink(String projectLink) {
//            this.projectLink = projectLink;
//        }
//
//        public int getSuperChapterId() {
//            return superChapterId;
//        }
//
//        public void setSuperChapterId(int superChapterId) {
//            this.superChapterId = superChapterId;
//        }
//
//        public String getSuperChapterName() {
//            return superChapterName;
//        }
//
//        public void setSuperChapterName(String superChapterName) {
//            this.superChapterName = superChapterName;
//        }
//
//        public long getPublishTime() {
//            return publishTime;
//        }
//
//        public void setPublishTime(long publishTime) {
//            this.publishTime = publishTime;
//        }
//
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
//
//        public int getVisible() {
//            return visible;
//        }
//
//        public void setVisible(int visible) {
//            this.visible = visible;
//        }
//
//        public int getZan() {
//            return zan;
//        }
//
//        public void setZan(int zan) {
//            this.zan = zan;
//        }

    }
}