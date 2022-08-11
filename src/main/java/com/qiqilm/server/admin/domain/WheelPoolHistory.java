package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 轮池对象 wheel_pool_history
 *
 * @author rajesh
 * @date 2022-07-29
 */

@Data
public class WheelPoolHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String memberId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickName;

    /** 头号 */
    @Excel(name = "头号")
    private Long headId;

    /** 地位 */
    @Excel(name = "地位")
    private int status;


    /** 第一 */
    @Excel(name = "第一")
    private int first;

    /** 奖 */
    @Excel(name = "奖")
    private BigDecimal prize;

    /** 获胜ID */
    @Excel(name = "获胜ID")
    private int winId;

    /** 奖牌类型 */
    @Excel(name = "奖牌类型")
    private String medalType;

    /** 绘制类型 */
    @Excel(name = "绘制类型")
    private String drawType;

    /** 位置 */
    @Excel(name = "位置")
    private String position;

    @Excel(name = "会员类型")
    private Integer memberStatus;

    @Excel(name = "获胜次数")
    private Integer winTimes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("memberId", getMemberId())
                .append("nickName", getNickName())
                .append("headId", getHeadId())
                .append("status", getStatus())
                .append("first", getFirst())
                .append("prize", getPrize())
                .append("winId", getWinId())
                .append("medalType", getMedalType())
                .append("drawType", getDrawType())
                .append("position", getPosition())
                .append("winTimes", getWinTimes())
                .toString();
    }

}
