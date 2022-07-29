package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

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

}
