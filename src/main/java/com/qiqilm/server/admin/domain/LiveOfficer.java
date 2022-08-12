package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LiveOfficer extends BaseEntity {

    /** 平台用户ID_hostID */
    private String id;

    /** 1=超管2=房管 */
    @Excel(name = "类型")
    private Long type;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long hostId;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String puserId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, databaseFormat = "yyyy-MM-dd HH:mm:ss")
    private Date cTime;

    private int status = 1;

    private String hostName;

    private String pUserName;
}
