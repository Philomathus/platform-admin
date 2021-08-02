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
 * 任务信息列表对象 activity_quest_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class ActivityQuestInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date ctime;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long indexs;

    /** 任务类型id */
    @Excel(name = "任务类型id")
    private String typeId;

    /** 目标任务量 */
    @Excel(name = "目标任务量")
    private Integer target;

    /** 完成后增加的资金 */
    @Excel(name = "完成后增加的资金")
    private BigDecimal reward;

    /** 任务详情 */
    @Excel(name = "任务详情")
    private String detail;

    /** 描述 */
    @Excel(name = "描述")
    private String content;

    /** 所属游戏id */
    @Excel(name = "所属游戏id")
    private String gameId;

    /** 平台游戏类型 */
    @Excel(name = "平台游戏类型")
    private String kindId;

    /** 平台类型 */
    @Excel(name = "平台类型")
    private Integer platformId;

    private String platformName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("icon", getIcon())
            .append("title", getTitle())
            .append("ctime", getCtime())
            .append("indexs", getIndexs())
            .append("typeId", getTypeId())
            .append("target", getTarget())
            .append("reward", getReward())
            .append("detail", getDetail())
            .append("content", getContent())
            .append("gameId", getGameId())
            .append("kindId", getKindId())
            .append("platformId", getPlatformId())
            .toString();
    }
}
