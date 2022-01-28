package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 lottery_history_dice
 *
 * @author 77tv
 * @date 2022-01-27
 */
@Data
public class LotteryHistoryDice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String pUserId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String name;

    /** 头像 */
    @Excel(name = "头像")
    private String headImg;

    /** 时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "时间", width = 30, databaseFormat = "yyyy-MM-dd")
    private Date cTime;

    /** 奖励 */
    @Excel(name = "奖励")
    private BigDecimal award;

    /** 活动类型 */
    @Excel(name = "活动类型")
    private Long type;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pUserId", getPUserId())
            .append("name", getName())
            .append("headImg", getHeadImg())
            .append("cTime", getCTime())
            .append("award", getAward())
            .append("type", getType())
            .toString();
    }
}
