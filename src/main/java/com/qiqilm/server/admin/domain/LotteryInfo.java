package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 彩票名称对象 lottery_info
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Data
public class LotteryInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 彩种编号 */
    private Long id;

    /** 彩种名称 */
    @Excel(name = "彩种名称")
    private String name;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 1 启用 0 禁用 */
    @Excel(name = "1 启用 0 禁用")
    private Long status;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 0=官方1=自开（数据库）2=自开（程序） */
    @Excel(name = "0=官方1=自开", suffix = "数=据库")
    private Long official;

    /** 杀率 */
    @Excel(name = "杀率")
    private BigDecimal killRate;


    /** 最小投注金额（小于则随机开奖） */
    @Excel(name = "最小投注金额", suffix = "小=于则随机开奖")
    private BigDecimal minCost;

    /** 周期 */
    @Excel(name = "周期")
    private Long cycle;

    /** 排序 */
    @Excel(name = "排序")
    private String order;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("type", getType())
            .append("status", getStatus())
            .append("icon", getIcon())
            .append("official", getOfficial())
            .append("killRate", getKillRate())
            .append("minCost", getMinCost())
            .append("order", getOrder())
            .append("cycle", getCycle())
            .toString();
    }
}
