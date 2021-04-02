package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 彩票注单对象 live_note
 *
 * @author 77tv
 * @date 2021-03-19
 */
@Data
public class LiveNote {
    private static final long serialVersionUID = 1L;

    /** 注单ID */
    private String id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String userId;

    /** 投资金额 */
    @Excel(name = "投资金额")
    private BigDecimal cost;


    /** 收益 */
    @Excel(name = "收益")
    private BigDecimal income;


    /** 道具(游戏)类型 */
    @Excel(name = "道具(游戏)类型")
    private String kindId;



    /** 主播ID */
    @Excel(name = "主播ID")
    private Integer tarId;


    /** 1=礼物2=游戏3=彩票 */
    @Excel(name = "1=礼物2=游戏3=彩票")
    private Integer type;

    /** 创建时间 */

    private String createTime;

    /** 结束时间 */
    private String endTime;

}
