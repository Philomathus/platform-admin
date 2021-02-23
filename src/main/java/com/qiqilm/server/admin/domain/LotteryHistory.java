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
 * 开奖历史对象 lottery_history
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Data
public class LotteryHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 期数 */
    private String issue;

    /** 所属彩种 */
    private Long lotteryId;

    /** 开奖号码 */
    private String code;

    /** 开奖时间 */
    private Date ktime;

    /** 0=投注中1=已开奖2=已派奖3=开奖失败 */
    private Long status;

    /** 彩票名称 */
    private String name;

    /** 自开实际杀率 */
    private BigDecimal killRate;

    /** 总投注 */
    private Long totalBet;

    /** 预计派奖总额 */
    private BigDecimal totalPrize;

    /** 0=未杀1=控杀 */
    private Long ctl;

    /** 开奖分析 */
    private String analyse;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("issue", getIssue())
            .append("lotteryId", getLotteryId())
            .append("code", getCode())
            .append("ktime", getKtime())
            .append("status", getStatus())
            .append("name", getName())
            .append("killRate", getKillRate())
            .append("totalBet", getTotalBet())
            .append("totalPrize", getTotalPrize())
            .append("ctl", getCtl())
            .append("analyse", getAnalyse())
            .toString();
    }
}
