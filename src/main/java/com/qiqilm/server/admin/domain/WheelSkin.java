package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 转盘皮肤列对象 wheel_skin
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Data
public class WheelSkin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 价格 */
    @Excel(name = "价格")
    private Long prize;

    /** 皮肤名称 */
    @Excel(name = "皮肤名称")
    private String skinName;

    /** 英雄名称 */
    @Excel(name = "英雄名称")
    private String heroName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("prize", getPrize())
            .append("skinName", getSkinName())
            .append("heroName", getHeroName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
