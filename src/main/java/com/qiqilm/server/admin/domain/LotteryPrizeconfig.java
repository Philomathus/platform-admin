package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 开奖配置对象 lottery_prizeconfig
 *
 * @author 77tv
 * @date 2021-03-18
 */
@Data
public class LotteryPrizeconfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 彩种ID */
    private String lotteryId;

    /** 彩种名称 */
    @Excel(name = "彩种名称")
    private String lotteryName;

    /** 杀率阀值 */
    @Excel(name = "杀率阀值")
    private BigDecimal lotteryKillrate;

    /** 杀率禁用时间点 */
    @Excel(name = "杀率禁用时间点")
    private String lotteryNokillratehour;

    /** 随机开启不杀概率 */
    @Excel(name = "随机开启不杀概率")
    private Long lotteryRandom;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("lotteryId", getLotteryId())
            .append("lotteryName", getLotteryName())
            .append("lotteryKillrate", getLotteryKillrate())
            .append("lotteryNokillratehour", getLotteryNokillratehour())
            .append("lotteryRandom", getLotteryRandom())
            .toString();
    }
}
