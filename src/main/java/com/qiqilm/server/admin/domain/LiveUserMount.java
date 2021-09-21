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
 * 直播间会员坐骑对象 live_user_mount
 *
 * @author 77tv
 * @date 2021-03-09
 */
@Data
public class LiveUserMount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private String userId;

    /** 过期时间 */
    @JsonFormat( pattern = "yyyy-MM-dd" )
    private Date effectiveTime;

    /** 坐骑id */
    @Excel(name = "坐骑id")
    private Integer mountId;

    /** 0:禁用;1:启用;默认启用 */
    @Excel(name = "0:禁用;1:启用;默认启用")
    private String isUse;

    /** 坐骑名称 */
    private String mountName;

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
            .append("userId", getUserId())
            .append("effectiveTime", getEffectiveTime())
            .append("mountId", getMountId())
            .append("isUse", getIsUse())
            .toString();
    }
}
