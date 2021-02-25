package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务信息列表对象 activity_quest_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
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
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date ctime;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long indexs;

    /** 任务类型id */
    @Excel(name = "任务类型id")
    private String typeId;

    /** 目标任务量 */
    @Excel(name = "目标任务量")
    private Long target;

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
    private Long platformId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getCtime() {
        return ctime;
    }
    public void setIndexs(Long indexs) {
        this.indexs = indexs;
    }

    public Long getIndexs() {
        return indexs;
    }
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }
    public void setTarget(Long target) {
        this.target = target;
    }

    public Long getTarget() {
        return target;
    }
    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public BigDecimal getReward() {
        return reward;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getKindId() {
        return kindId;
    }
    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getPlatformId() {
        return platformId;
    }

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
