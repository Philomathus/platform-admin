package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主播投诉记录对象 live_complaint
 *
 * @author 77tv
 * @date 2021-09-14
 */
@Data
public class LiveComplaint extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 投诉内容 */
    @Excel(name = "投诉内容")
    private String content;

    /** 用户手机号 */
    @Excel(name = "用户手机号")
    private String mobile;

    /** 房间名称 */
    @Excel(name = "房间名称")
    private String roomName;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String anchorNick;

    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private String anchor;

    /** 审批备注 */
    @Excel(name = "审批备注")
    private String comments;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, databaseFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processingTime;

    /** 处理状态(0未处理 1已处理 2驳回) */
    @Excel(name = "处理状态(0未处理 1已处理 2驳回)")
    private String status;

    @JsonIgnore
    private String[] selectDate;
    @JsonIgnore
    private String   selectStartDate;
    @JsonIgnore
    private String   selectEndDate;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("mobile", getMobile())
            .append("roomName", getRoomName())
            .append("anchorNick", getAnchorNick())
            .append("userId", getUserId())
            .append("anchor", getAnchor())
            .append("comments", getComments())
            .append("approver", getApprover())
            .append("processingTime", getProcessingTime())
            .append("createTime", getCreateTime())
            .append("status", getStatus())
            .toString();
    }
}
