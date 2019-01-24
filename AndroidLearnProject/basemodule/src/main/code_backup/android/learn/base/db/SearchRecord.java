package com.android.learn.base.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SearchRecord {
    @Id
    private Long id;
    private String name;
    @Generated(hash = 2029079304)
    public SearchRecord(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 839789598)
    public SearchRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

}