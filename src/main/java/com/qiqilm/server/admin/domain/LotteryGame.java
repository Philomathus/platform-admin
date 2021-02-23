package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 下注对象 lottery_game
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Data
public class LotteryGame extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键（uuid-36） */
    private String id;

    /** 菜单id */
    private String methodId;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 简介 */
    @Excel(name = "简介")
    private String info;


    /** 赔率 */
    @Excel(name = "赔率")
    private BigDecimal odds;

    /** 获奖规则 */
    @Excel(name = "获奖规则")
    private String victoryRule;

    /** 唯一编号 */
    @Excel(name = "唯一编号")
    private Long ind;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("methodId", getMethodId())
            .append("type", getType())
            .append("info", getInfo())
            .append("odds", getOdds())
            .append("victoryRule", getVictoryRule())
            .append("ind", getInd())
            .toString();
    }
}
