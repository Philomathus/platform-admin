package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主播投诉记录对象 live_complaint
 *
 * @author 77tv
 * @date 2021-08-14
 */
@Data
public class LiveComplaint extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户 */
    @Excel(name = "用户ID")
    private String userId;

    @Excel(name = "房间名称")
    private String roomName;

    @Excel(name = "手机号")
    private String mobile;

    @Excel(name = "处理状态")
    private String status;

    @Excel(name = "主播ID")
    private String anchor;

    @Excel(name = "主播昵称")
    private String anchorNick;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, databaseFormat = "yyyy-MM-dd")
    private Date createTime;

    @Excel(name = "投诉内容")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, databaseFormat = "yyyy-MM-dd")
    private Date processingTime;

    @Excel(name = "审批备注")
    private String comments;

    @Excel(name = "审批人")
    private String approver;

    @JsonIgnore
    private String[] selectDate;
    @JsonIgnore
    private String   selectStartDate;
    @JsonIgnore
    private String   selectEndDate;

}
