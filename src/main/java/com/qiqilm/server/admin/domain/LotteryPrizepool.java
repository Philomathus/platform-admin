package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 奖池配置对象 lottery_prizepool
 *
 * @author 77tv
 * @date 2021-03-18
 */
@Data
public class LotteryPrizepool extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID主键 */
    private String id;

    /** 彩种编号 */
    @Excel(name = "彩种编号")
    private Long lotteryId;

    /** 彩种编号 */
    @Excel(name = "彩种名称")
    private String lotteryName;

    /** 奖池日期 */
    @Excel(name = "奖池日期")
    private String lotteryDate;

    /** 奖池日期小时 */
    @Excel(name = "奖池日期小时")
    private Long lotteryHour;

    /** 奖池投注日累积 */
    @Excel(name = "奖池投注日累积")
    private BigDecimal pTzTotal;

    /** 奖池派奖日累积 */
    @Excel(name = "奖池派奖日累积")
    private BigDecimal pPjTotal;

    /** 奖池剩余金额日累积 */
    @Excel(name = "奖池剩余金额日累积")
    private BigDecimal pSyTotal;

    /** 累积杀率 */
    @Excel(name = "累积杀率")
    private BigDecimal pKillrate;

    /** 游戏奖池使用金额 */
    @Excel(name = "游戏奖池使用金额")
    private BigDecimal poolUsemoney;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotteryId", getLotteryId())
            .append("lotteryName", getLotteryName())
            .append("lotteryDate", getLotteryDate())
            .append("lotteryHour", getLotteryHour())
            .append("pTzTotal", getPTzTotal())
            .append("pPjTotal", getPPjTotal())
            .append("pSyTotal", getPSyTotal())
            .append("pKillrate", getPKillrate())
            .append("poolUsemoney", getPoolUsemoney())
            .toString();
    }
}
