package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 站内信息对象 message_on_site
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class MessageOnSite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 信息标题 */
    @Excel(name = "信息标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 接收者类型 */
    @Excel(name = "接收者类型")
    private String receiverType;

    /** 接收者 */
    @Excel(name = "接收者")
    private String receiver;

    /** 动作 */
    @Excel(name = "动作")
    private String action;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date pubdatetime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public String getReceiverType() {
        return receiverType;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
    public void setPubdatetime(Date pubdatetime) {
        this.pubdatetime = pubdatetime;
    }

    public Date getPubdatetime() {
        return pubdatetime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("receiverType", getReceiverType())
            .append("receiver", getReceiver())
            .append("action", getAction())
            .append("pubdatetime", getPubdatetime())
            .toString();
    }
}
