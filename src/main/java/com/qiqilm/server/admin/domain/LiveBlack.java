package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 拉黑对象 live_black
 *
 * @author 77tv
 * @date 2021-08-24
 */
@Data
public class LiveBlack  {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long hostId;

    /** 被设置的用户ID */
    @Excel(name = "被设置的用户ID")
    private String blackUserId;
    @Excel(name = "黑名单备注")
    private String remark;
    private Integer vip;
    private String nickName;
    private String hostName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("hostId", getHostId())
                .append("blackUserId", getBlackUserId())
                .append("remark", getRemark())
                .toString();
    }
}