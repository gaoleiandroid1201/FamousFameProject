package com.android.learn.mcontract;



import com.android.learn.base.mmodel.TreeBean;

import java.util.List;


public class KnowledgeContract {

    public interface View  {
        void showKnowledge(List<TreeBean> data);

    }

    public interface Presenter {
        void getKnowledge();

    }

}
