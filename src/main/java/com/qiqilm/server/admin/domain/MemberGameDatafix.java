package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_game_datafix
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
public class MemberGameDatafix extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 本地ID */
    private String id;

    /** 账号 */
    @Excel(name = "账号")
    private String userId;

    /** 游戏开始时间 */
    @Excel(name = "游戏开始时间")
    private String gameStartTime;

    /** 游戏结束时间 */
    @Excel(name = "游戏结束时间")
    private String gameEndTime;

    /** 本地平台id */
    @Excel(name = "本地平台id")
    private Long platformId;

    /** 0:未处理1已处理 */
    @Excel(name = "0:未处理1已处理")
    private Integer status;

    private String  platformName;





    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("gameStartTime", getGameStartTime())
            .append("gameEndTime", getGameEndTime())
            .append("platformId", getPlatformId())
            .append("status", getStatus())
            .toString();
    }
}