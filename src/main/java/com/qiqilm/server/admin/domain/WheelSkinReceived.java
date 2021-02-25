package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 转盘皮肤领取对象 wheel_skin_received
 *
 * @author 77tv
 * @date 2021-02-24
 */
@Data
public class WheelSkinReceived extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 抽奖历史id */
    @Excel(name = "抽奖历史id")
    private Long wheelHistoryId;

    /** 用户id */
    @Excel(name = "用户id")
    private String pUserId;

    /** 游戏大区 */
    @Excel(name = "游戏大区")
    private String daQu;

    /** 游戏昵称 */
    @Excel(name = "游戏昵称")
    private String name;

    /** 游戏昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 皮肤名称 */
    @Excel(name = "皮肤名称")
    private String skin;

    @Excel(name = "中奖金额")
    private Integer prize;

    /** 领取状态 0未领取1领取中2已领取 */
    @Excel(name = "领取状态 0未领取1领取中2已领取")
    private Long receiveType;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wheelHistoryId", getWheelHistoryId())
            .append("pUserId", getPUserId())
            .append("daQu", getDaQu())
            .append("name", getName())
            .append("skin", getSkin())
            .append("receiveType", getReceiveType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
