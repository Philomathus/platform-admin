package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 系统公告对象 home_notice
 *
 * @author 77tv
 * @date 2021-02-07
 */
@Data
public class HomeNotice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 排序 */
    @Excel(name = "排序")
    private Integer indexs;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 类型 */
    @Excel(name = "类型")
    private Integer type;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("updateTime", getUpdateTime())
            .append("indexs", getIndexs())
            .append("status", getStatus())
            .append("type", getType())
            .toString();
    }
}
