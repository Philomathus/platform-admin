package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 wheel_user_dice
 *
 * @author 77tv
 * @date 2021-09-02
 */
@Data
public class WheelUserDice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 平台会员ID */
    private String id;

    /** 剩余次数 */
    @Excel(name = "剩余次数")
    private Integer times;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("times", getTimes())
            .toString();
    }
}
