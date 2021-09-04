package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 博饼中奖记录对象 wheel_history_dice
 *
 * @author 77tv
 * @date 2021-09-02
 */
@Data
public class WheelHistoryDice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String pUserId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String name;

    /** 奖项 */
    @Excel(name = "奖项")
    private String diceName;

    /** 时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date cTime;

    /** 骰数值 */
    @Excel(name = "骰数值")
    private String diceValue;

    /** 奖项id */
    @Excel(name = "奖项id")
    private Long diceId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pUserId", getPUserId())
            .append("name", getName())
            .append("diceName", getDiceName())
            .append("cTime", getCTime())
            .append("diceValue", getDiceValue())
            .append("diceId", getDiceId())
            .toString();
    }
}
