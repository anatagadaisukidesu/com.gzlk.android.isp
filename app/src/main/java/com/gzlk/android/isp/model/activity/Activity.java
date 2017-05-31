package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.ACTIVITY)
public class Activity extends Model {

    public interface Table {
        String ACTIVITY = "activity";
        String ACTIVITY_ARCHIVE = "activityArchive";
        String ASSOCIATOR = "associator";
        String CHARACTER = "character";
        String LABEL = "activityLabel";
        String NOTICE = "activityNotice";
        String VOTE = "vote";
        String VOTE_ITEM = "voteItem";
        String VOTE_RECORD = "voteRecord";
    }

    public interface Field {
        String UseMessage = "useMessage";
        String Status = "status";
        String OpenStatus = "openStatus";
        String NimId = "nimId";
        String MemberIdArray = "memberIdArray";
        String MemberNameArray = "memberNameArray";
        String BeginDate = "beginDate";
        String Site = "site";
        String ActivityId = "activityId";
    }

    /**
     * 活动状态
     */
    public interface Status {
        /**
         * 开放中
         */
        int ACTIVE = 1;
        /**
         * 已结束
         */
        int ENDED = 2;
    }

    //标题
    @Column(Archive.Field.Title)
    private String title;
    //内容
    @Column(Archive.Field.Content)
    private String content;
    //组织id
    @Column(Organization.Field.GroupId)
    private String groupId;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //最后修改时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;
    //是否使用短信通知
    @Column(Field.UseMessage)
    private boolean useMsg;
    //宣传照h
    @Column(Archive.Field.Image)
    private String img;
    //状态：1.活跃 2.结束
    @Column(Field.Status)
    private int status;
    //活动公开状态 1.向所有人公开 2。关闭（只向组织内公开） 参见ActivityConstant中的状态定义
    @Column(Field.OpenStatus)
    private int openStatus;
    //标签
    @Column(Archive.Field.Label)
    private ArrayList<String> label;
    //创建者
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名字
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //历康活动对应于网易云的聊天群的tid
    @Column(Field.NimId)
    private String tid;
    //邀请的成员id列表（可能是尚未加入活动）,内容格式如["aaa","bbb"]
    @Column(Field.MemberIdArray)
    private ArrayList<String> memberIdArray;
    //邀请的成员姓名列表（可能是尚未加入活动）,内容格式如["aaa","bbb"]
    @Column(Field.MemberNameArray)
    private ArrayList<String> memberNameArray;
    //所属组织
    @Ignore
    private Organization group;

    //活动开始时间
    @Column(Field.BeginDate)
    private String beginDate;
    //活动地点
    @Column(Field.Site)
    private String site;
    //活动附件
    @Ignore
    private ArrayList<Attachment> attUrlArray;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public boolean isUseMsg() {
        return useMsg;
    }

    public void setUseMsg(boolean useMsg) {
        this.useMsg = useMsg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(int openStatus) {
        this.openStatus = openStatus;
    }

    public ArrayList<String> getLabel() {
        return label;
    }

    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public ArrayList<String> getMemberIdArray() {
        return memberIdArray;
    }

    public void setMemberIdArray(ArrayList<String> memberIdArray) {
        this.memberIdArray = memberIdArray;
    }

    public ArrayList<String> getMemberNameArray() {
        return memberNameArray;
    }

    public void setMemberNameArray(ArrayList<String> memberNameArray) {
        this.memberNameArray = memberNameArray;
    }

    public Organization getGroup() {
        if (null == group) {
            group = new Dao<>(Organization.class).query(groupId);
        }
        return group;
    }

    public void setGroup(Organization group) {
        this.group = group;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public ArrayList<Attachment> getAttUrlArray() {
        return attUrlArray;
    }

    public void setAttUrlArray(ArrayList<Attachment> attUrlArray) {
        this.attUrlArray = attUrlArray;
    }
}
