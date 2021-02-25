package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 活动信息对象 activity_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
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
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getCtime() {
        return ctime;
    }
    public void setIndexs(Long indexs) {
        this.indexs = indexs;
    }

    public Long getIndexs() {
        return indexs;
    }
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

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
            .toString();
    }
}
