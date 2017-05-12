package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>群邀请<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.INVITATION)
public class Invitation extends Model {

    public static class Field {
        public static final String InviterId = "inviterId";
        public static final String InviterName = "inviterName";
        public static final String InviteeId = "inviteeId";
        public static final String InviteeName = "inviteeName";
        public static final String AllowSee = "allowSeeInviterBaseInfo";
        public static final String HandleTime = "handleTime";
        public static final String Message = "message";
        public static final String State = "state";
    }

    @Column(Organization.Field.GroupId)
    private String groupId;
    @Column(Organization.Field.GroupName)
    private String groupName;
    //邀请人ID和姓名
    @Column(Field.InviterId)
    private String inviterId;
    @Column(Field.InviterName)
    private String inviterName;
    //被邀请人ID和姓名
    @Column(Field.InviteeId)
    private String inviteeId;
    @Column(Field.InviteeName)
    private String inviteeName;
    //邀请时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //是否允许被邀请人查看群体基本信息
    @Column(Field.AllowSee)
    private String allowSeeInviterBaseInfo;
    //处理时间
    @Column(Field.HandleTime)
    private String handleTime;
    //留言
    @Column(Field.Message)
    private String msg;
    //处理状态：1未处理，2已处理，3已失效
    @Column(Field.State)
    private String state;
    //uuid是作为联结业务流消息和显示消息的字段，可被视为是Message表的外键
    @Column(Model.Field.UUID)
    private String uuid;
    @Column(Model.Field.AccessToken)
    private String accessToken;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(String inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAllowSeeInviterBaseInfo() {
        return allowSeeInviterBaseInfo;
    }

    public void setAllowSeeInviterBaseInfo(String allowSeeInviterBaseInfo) {
        this.allowSeeInviterBaseInfo = allowSeeInviterBaseInfo;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
