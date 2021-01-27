package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 会员发言对象 live_video_chat
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class LiveVideoChat extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long poscatId;

    /** 消息所在聊天组 */
    @Excel(name = "消息所在聊天组")
    private String group;

    /** 发送者id */
    @Excel(name = "发送者id")
    private Long userId;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String msg;

    /** 消息类型 0 普通消息 1 弹幕消息 */
    @Excel(name = "消息类型 0 普通消息 1 弹幕消息")
    private Integer type;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String poscatNickName;

    /** 发送者昵称 */
    @Excel(name = "发送者昵称")
    private String userNickName;

    /**
     * 发送开始时间
     */
    private String sendStartTime;

    /**
     * 发送结束时间
     */
    private String sendEndTime;

    /** 平台会员ID */
    @Excel(name = "平台会员ID")
    private String fromPlatform;


    public String getSendStartTime() {
        return sendStartTime;
    }

    public void setSendStartTime(String sendStartTime) {
        this.sendStartTime = sendStartTime;
    }

    public String getSendEndTime() {
        return sendEndTime;
    }

    public void setSendEndTime(String sendEndTime) {
        this.sendEndTime = sendEndTime;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setPoscatId(Long poscatId) {
        this.poscatId = poscatId;
    }

    public Long getPoscatId() {
        return poscatId;
    }
    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
    public void setPoscatNickName(String poscatNickName) {
        this.poscatNickName = poscatNickName;
    }

    public String getPoscatNickName() {
        return poscatNickName;
    }
    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserNickName() {
        return userNickName;
    }
    public void setFromPlatform(String fromPlatform) {
        this.fromPlatform = fromPlatform;
    }

    public String getFromPlatform() {
        return fromPlatform;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("poscatId", getPoscatId())
            .append("group", getGroup())
            .append("userId", getUserId())
            .append("msg", getMsg())
            .append("createTime", getCreateTime())
            .append("type", getType())
            .append("poscatNickName", getPoscatNickName())
            .append("userNickName", getUserNickName())
            .append("fromPlatform", getFromPlatform())
            .toString();
    }
}
