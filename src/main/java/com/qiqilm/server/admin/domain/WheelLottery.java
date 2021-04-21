package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转盘彩票对象 wheel_lottery
 *
 * @author 77tv
 * @date 2021-03-01
 */
@Data
public class WheelLottery extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 彩票ID-日期 */
    private String id;

    /** 彩票ID */
    @Excel(name = "彩票ID")
    private Long lotteryId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30,  exportFormat = "yyyy-MM-dd")
    private Date start;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30,  exportFormat = "yyyy-MM-dd")
    private Date end;

    /** 派奖比例（废弃） */
    @Excel(name = "派奖比例")
    private BigDecimal pRate;

    /** 奖金池最小生效废弃） */
    @Excel(name = "奖金池最小生效废弃）")
    private BigDecimal minPrize;

    /** 最小投注 */
    @Excel(name = "最小投注")
    private BigDecimal minBet;

    /** 预备派奖金额 */
    @Excel(name = "预备派奖金额")
    private BigDecimal prePrize;

    /** 实际派奖金额 */
    @Excel(name = "实际派奖金额")
    private BigDecimal actPrize;

    /** wheel_type为0是抽奖转盘,1是皮肤转盘 */
    @Excel(name = "wheel_type为0是抽奖转盘,1是皮肤转盘")
    private Long wheelType;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotteryId", getLotteryId())
            .append("name", getName())
            .append("start", getStart())
            .append("end", getEnd())
            .append("pRate", getPRate())
            .append("minPrize", getMinPrize())
            .append("minBet", getMinBet())
            .append("prePrize", getPrePrize())
            .append("actPrize", getActPrize())
            .append("wheelType", getWheelType())
            .toString();
    }
}
