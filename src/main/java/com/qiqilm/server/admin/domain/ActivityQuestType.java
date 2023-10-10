package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务类型对象 activity_quest_type
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class ActivityQuestType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 所属游戏id */
    @Excel(name = "所属游戏id")
    private String gameId;

    /** 排序 从小到大顺序 */
    @Excel(name = "排序 从小到大顺序")
    private Integer sort;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public Integer getSort() {
        return sort;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("gameId", getGameId())
            .append("sort", getSort())
            .toString();
    }
}
