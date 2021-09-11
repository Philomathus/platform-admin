package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客服投诉对象 chat_complaint
 *
 * @author 77tv
 * @date 2021-09-10
 */
@Data
public class ChatComplaint extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 发起人id */
    @Excel(name = "发起人id")
    private String userId;

    /** 客服id */
    @Excel(name = "客服id")
    private String kfId;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processingTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批备注 */
    @Excel(name = "审批备注")
    private String comments;

    /** 审批备注 */
    @Excel(name = "客服代充账号")
    private String account;

    /** 处理状态(0未处理 1已处理 2驳回) */
    @Excel(name = "处理状态(0未处理 1已处理 2驳回)")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("remark", getRemark())
            .append("userId", getUserId())
            .append("kfId", getKfId())
            .append("processingTime", getProcessingTime())
            .append("approver", getApprover())
            .append("comments", getComments())
            .append("createTime", getCreateTime())
            .append("status", getStatus())
            .toString();
    }
}
