package com.gzlk.android.isp.api.query;

import com.gzlk.android.isp.api.Api;
import com.gzlk.android.isp.model.activity.topic.AppTopic;
import com.gzlk.android.isp.model.user.SimpleMoment;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>网络请求返回的数据基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 19:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 19:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SingleQuery<T> extends Api<T> {

    protected T data;
    private ArrayList<SimpleMoment> userMmt;
    private int actInvtStatus;
    private int invtNum;
    private ArrayList<AppTopic>actTopicList;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<SimpleMoment> getUserMmt() {
        return userMmt;
    }

    public void setUserMmt(ArrayList<SimpleMoment> userMmt) {
        this.userMmt = userMmt;
    }

    public int getActInvtStatus() {
        return actInvtStatus;
    }

    public void setActInvtStatus(int actInvtStatus) {
        this.actInvtStatus = actInvtStatus;
    }

    public int getInvtNum() {
        return invtNum;
    }

    public void setInvtNum(int invtNum) {
        this.invtNum = invtNum;
    }

    public ArrayList<AppTopic> getActTopicList() {
        return actTopicList;
    }

    public void setActTopicList(ArrayList<AppTopic> actTopicList) {
        this.actTopicList = actTopicList;
    }
}
