package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 转盘中奖历史对象 wheel_history
 *
 * @author 77tv
 * @date 2021-03-05
 */
@Data
public class WheelHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String pUserId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String name;

    /** 奖励 */
    @Excel(name = "奖励")
    private Long prize;

    /** 时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "时间", width = 30, exportFormat = "yyyy-MM-dd")
    private Date cTime;

    private String[] selectDate = new String[2];
    private String sTime;
    private String eTime;

    /** type为0是抽奖转盘,1是皮肤转盘 */
    @Excel(name = "type为0是抽奖转盘,1是皮肤转盘")
    private Long wheelType;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pUserId", getPUserId())
            .append("name", getName())
            .append("prize", getPrize())
            .append("cTime", getCTime())
            .append("wheelType", getWheelType())
            .toString();
    }
}
