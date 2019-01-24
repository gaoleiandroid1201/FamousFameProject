package com.android.learn.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.learn.base.greendao.gen.DaoMaster;
import com.android.learn.base.greendao.gen.DaoSession;
import com.android.learn.base.greendao.gen.SearchRecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DBManager {
    private final static String dbName = "search_record";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }
    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }
    /**
     * 插入一条记录
     *
     * @param searchRecord
     */
    public void insertUser(SearchRecord searchRecord) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        userDao.insert(searchRecord);
    }

    /**
     * 插入用户集合
     *
     * @param users
     */
    public void insertUserList(List<SearchRecord> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        userDao.insertInTx(users);
    }
    /**
     * 删除一条记录
     *
     * @param searchRecord
     */
    public void deleteUser(SearchRecord searchRecord) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        userDao.delete(searchRecord);
    }
    public void deleteAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        userDao.deleteAll();
    }

    /**
     * 更新一条记录
     *
     * @param searchRecord
     */
    public void updateUser(SearchRecord searchRecord) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        userDao.update(searchRecord);
    }
    /**
     * 查询用户列表
     */
    public List<SearchRecord> queryUserList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchRecordDao userDao = daoSession.getSearchRecordDao();
        QueryBuilder<SearchRecord> qb = userDao.queryBuilder();
        List<SearchRecord> list = qb.list();
        return list;
    }

   

}