package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 系统日志对象 live_log
 *
 * @author 77tv
 * @date 2021-04-05
 */
@Data
public class LiveLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 日志信息 */
    @Excel(name = "日志信息")
    private String logInfo;

    /** 日志时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日志时间", width = 30, databaseFormat = "yyyy-MM-dd")
    private Date logTime;

    /** 日志类型 */
    @Excel(name = "日志类型")
    private Long logType;

    /** 日志操作员 */
    @Excel(name = "日志操作员")
    private String logAdmin;

    /** 日志IP */
    @Excel(name = "日志IP")
    private String logIp;

    /** 日志状态 */
    @Excel(name = "日志状态")
    private Integer logStatus;

    /** 模块 */
    @Excel(name = "模块")
    private String module;

    /** 方法 */
    @Excel(name = "方法")
    private String action;

    /** 日志操作员昵称 */
    @Excel(name = "日志操作员昵称")
    private String logAdminNickName;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String bussinessType;

    /** 用户平台ID */
    @Excel(name = "用户平台ID")
    private String userPlatformId;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String userNickName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logInfo", getLogInfo())
            .append("logTime", getLogTime())
            .append("logType", getLogType())
            .append("logAdmin", getLogAdmin())
            .append("logIp", getLogIp())
            .append("logStatus", getLogStatus())
            .append("module", getModule())
            .append("action", getAction())
            .append("logAdminNickName", getLogAdminNickName())
            .append("bussinessType", getBussinessType())
            .append("userPlatformId", getUserPlatformId())
            .append("userNickName", getUserNickName())
            .toString();
    }
}