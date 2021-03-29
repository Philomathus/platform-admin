package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 live_host_wage_day
 *
 * @author 77tv
 * @date 2021-03-29
 */
@Data
public class LiveHostWageDay extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 日期+主播ID */
    private String id;

    /** 家族ID */
    @Excel(name = "家族ID")
    private Long familyId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long hostId;

    /** 直播开始时间 */
    @Excel(name = "直播开始时间")
    private String startTime;

    /** 直播结束时间 */
    @Excel(name = "直播结束时间")
    private String endTime;

    /** 直播时长（秒） */
    @Excel(name = "直播时长")
    private Integer liveTimeSec;

    /** 主播直播结算印票 */
    @Excel(name = "主播直播结算印票")
    private BigDecimal ticket;

    /** 上播次数 */
    @Excel(name = "上播次数")
    private Integer times;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("familyId", getFamilyId())
            .append("hostId", getHostId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("liveTimeSec", getLiveTimeSec())
            .append("ticket", getTicket())
            .append("times", getTimes())
            .toString();
    }
}