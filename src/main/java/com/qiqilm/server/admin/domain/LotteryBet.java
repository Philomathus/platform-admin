package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户投资行为对象 lottery_bet
 *
 * @author 77tv
 * @date 2021-02-03
 */
@Data
public class LotteryBet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 下注彩种id */
    @Excel(name = "下注彩种id")
    private String lotteryId;

    /** 下注期数 */
    @Excel(name = "下注期数")
    private String issue;

    /** 下注选择菜单 */
    @Excel(name = "下注选择菜单")
    private String methodId;

    /** 下注选择 */
    @Excel(name = "下注选择")
    private String betSelect;

    /** 筹码 */
    @Excel(name = "筹码")
    private BigDecimal chip;

    /** 下注时间 */

    private String betTime;

    /** 下注时间 */

    private String updateTime;


    /** 中奖金额 */
    @Excel(name = "中奖金额")
    private BigDecimal prize;

    /** 投资 */
    @Excel(name = "投资")
    private BigDecimal cost;

    /** 彩票名称 */
    @Excel(name = "彩票名称")
    private String lotteryName;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String puserId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Integer anchor;

    /** 开奖号码 */
    @Excel(name = "开奖号码")
    private String code;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("lotteryId", getLotteryId())
                .append("issue", getIssue())
                .append("methodId", getMethodId())
                .append("betSelect", getBetSelect())
                .append("chip", getChip())
                .append("betTime", getBetTime())
                .append("prize", getPrize())
                .append("cost", getCost())
                .append("lotteryName", getLotteryName())
                .append("puserId", getPuserId())
                .append("anchor", getAnchor())
                .append("code", getCode())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
