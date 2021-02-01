package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 game_type_with
 *
 * @author 77tv
 * @date 2021-02-01
 */
public class GameTypeWith extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 类别编号 */
    @Excel(name = "类别编号")
    private String typeId;

    /** 游戏编号 */
    @Excel(name = "游戏编号")
    private String gameId;

    /** 平台游戏编号 */
    @Excel(name = "平台游戏编号")
    private String kindId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("typeId", getTypeId())
            .append("gameId", getGameId())
            .append("createTime", getCreateTime())
            .append("kindId", getKindId())
            .toString();
    }
}