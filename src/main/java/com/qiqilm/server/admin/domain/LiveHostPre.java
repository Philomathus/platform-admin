package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主播开播时间预约对象 live_host_pre
 *
 * @author 77tv
 * @date 2021-04-13
 */
@Data
public class LiveHostPre extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 日期+主播ID */
    private String id;

    private String dateDay;

    private String live;

    /** 家族ID */
    @Excel(name = "家族ID")
    private Long familyId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long hostId;

    /** 直播昵称 */
    @Excel(name = "直播昵称")
    private String hostName;

    /** 0=申请1通过2=驳回 */
    @Excel(name = "0=申请1通过2=驳回")
    private Long status;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live0;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live1;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live2;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live3;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live4;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live5;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live6;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live7;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live8;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live9;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live10;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live11;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live12;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live13;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live14;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live15;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live16;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live17;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live18;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private String live19;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private Long live20;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private String live21;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private String live22;

    /** 直播时间 */
    @Excel(name = "直播时间")
    private String live23;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("familyId", getFamilyId())
            .append("hostId", getHostId())
            .append("hostName", getHostName())
            .append("status", getStatus())
            .append("live0", getLive0())
            .append("live1", getLive1())
            .append("live2", getLive2())
            .append("live3", getLive3())
            .append("live4", getLive4())
            .append("live5", getLive5())
            .append("live6", getLive6())
            .append("live7", getLive7())
            .append("live8", getLive8())
            .append("live9", getLive9())
            .append("live10", getLive10())
            .append("live11", getLive11())
            .append("live12", getLive12())
            .append("live13", getLive13())
            .append("live14", getLive14())
            .append("live15", getLive15())
            .append("live16", getLive16())
            .append("live17", getLive17())
            .append("live18", getLive18())
            .append("live19", getLive19())
            .append("live20", getLive20())
            .append("live21", getLive21())
            .append("live22", getLive22())
            .append("live23", getLive23())
            .toString();
    }
}
