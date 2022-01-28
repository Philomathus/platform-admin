package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 【请填写功能名称】对象 lottery_dice_user
 *
 * @author 77tv
 * @date 2022-01-26
 */
@Data
public class LotteryDiceUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 平台会员ID */
    private String id;

    /** 剩余次数 */
    @Excel(name = "剩余次数")
    private Integer times;

    /** 活动类型 */
    @Excel(name = "活动类型")
    private Integer type;

    private BigDecimal rechargeMoney;

}
