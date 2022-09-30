package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * lottery Count
 *
 * @author rajesh
 * @date 2022-09-30
 */
@Data
public class LotteryCount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private Long id;

    /**agent */
    @Excel(name = "agent")
    private String agent;

    /**秒钱 */
    @Excel(name = "彩票名称")
    private Long lotteryId;

    /**会员ID */
    @Excel(name = "会员ID")
    private String pUserId;

    /**期数 */
    @Excel(name = "期数")
    private String issue;

    /**下注内容 */
    @Excel(name = "下注内容")
    private String betInfo;

    /**下注金额 */
    @Excel(name = "下注金额")
    private String chip;

    /**下注IP */
    @Excel(name = "下注IP")
    private String ip;

    /**秒钱 */
    @Excel(name = "彩票名称")
    private String lotteryName;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("agent", getAgent())
                .append("lotteryId", getLotteryId())
                .append("pUserId", getPUserId())
                .append("issue", getIssue())
                .append("betInfo", getBetInfo())
                .append("chip", getChip())
                .append("ip", getChip())
                .toString();
    }
}
