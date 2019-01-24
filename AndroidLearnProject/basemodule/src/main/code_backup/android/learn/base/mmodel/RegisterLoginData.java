package com.android.learn.base.mmodel;

import java.util.List;

/**
 * @author quchao
 * @date 2018/2/26
 */

public class RegisterLoginData extends BaseData {

   public Data data;

    public class Data {
        public String username;
        public String password;
        public String email, icon, token;
        public int id, type;
        //    private List<Integer> chapterTops;
        public List<Integer> collectIds;

        @Override
        public String toString() {
            return "RegisterLoginData{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +
                    ", icon='" + icon + '\'' +
                    ", token='" + token + '\'' +
                    ", id=" + id +
                    ", type=" + type +
                    ", collectIds=" + collectIds +
                    '}';
        }
    }
}
