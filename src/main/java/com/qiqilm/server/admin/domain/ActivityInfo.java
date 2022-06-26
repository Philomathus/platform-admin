package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 活动信息对象 activity_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class ActivityInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date ctime;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long indexs;

    /** 活动类型id */
    @Excel(name = "活动类型id")
    private String typeId;

    /** 活动详情 */
    @Excel(name = "活动详情")
    private String content;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 0=活动详情 1=图标跳转链接 */
    @Excel(name = "0=活动详情 1=图标跳转链接")
    private Integer type;

    /** 图标跳转链接*/
    @Excel(name = "图标跳转链接")
    private String url;

    @Excel(name = "排序")
    private String order;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("icon", getIcon())
            .append("title", getTitle())
            .append("ctime", getCtime())
            .append("indexs", getIndexs())
            .append("typeId", getTypeId())
            .append("content", getContent())
                .append("order", getContent())
            .toString();
    }
}
