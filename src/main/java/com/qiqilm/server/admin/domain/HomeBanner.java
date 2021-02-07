package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 首页轮播图对象 home_banner
 *
 * @author 77tv
 * @date 2021-02-07
 */
@Data
public class HomeBanner extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer indexs;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 图片 */
    @Excel(name = "图片")
    private String coverImg;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("indexs", getIndexs())
            .append("status", getStatus())
            .append("updateTime", getUpdateTime())
            .append("coverImg", getCoverImg())
            .toString();
    }
}
