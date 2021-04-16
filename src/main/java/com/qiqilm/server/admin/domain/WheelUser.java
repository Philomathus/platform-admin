package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 转盘用户对象 wheel_user
 *
 * @author 77tv
 * @date 2021-03-08
 */
@Data
public class WheelUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 平台会员ID */
    private String id;

    /** 剩余次数 */
    @Excel(name = "剩余次数")
    private Integer times;

    /** 皮肤转盘剩余次数 */
    @Excel(name = "皮肤转盘剩余次数")
    private Integer skinTimes;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("times", getTimes())
            .append("skinTimes", getSkinTimes())
            .toString();
    }
}
