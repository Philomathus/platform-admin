package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 用户层级 */
    @Excel(name = "用户层级")
    private Long mountId;

    /** 0:禁用;1:启用;默认启用 */
    @Excel(name = "0:禁用;1:启用;默认启用")
    private Long isUse;

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
