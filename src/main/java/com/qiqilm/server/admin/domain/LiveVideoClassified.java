package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 分类对象 live_video_classified
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class LiveVideoClassified extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String title;

    /** 是否有效 1-有效 0-无效 */
    @Excel(name = "是否有效 1-有效 0-无效")
    private Integer isEffect;

    /** 从大到小排 */
    @Excel(name = "从大到小排")
    private Long sort;

    /** 分类图标 */
    @Excel(name = "分类图标")
    private String img;

    /** 分类id */
    @Excel(name = "分类id")
    private Long classfy;

    /** 是否主播端显示 */
    @Excel(name = "是否主播端显示")
    private Integer isHostShow;

    /** 查询主播列表分页逻辑 */
    @Excel(name = "查询主播列表分页逻辑")
    private String sortDesc;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setIsEffect(Integer isEffect) {
        this.isEffect = isEffect;
    }

    public Integer getIsEffect() {
        return isEffect;
    }
    public void setSort(Long sort) {
        this.sort = sort;
    }

    public Long getSort() {
        return sort;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
    public void setClassfy(Long classfy) {
        this.classfy = classfy;
    }

    public Long getClassfy() {
        return classfy;
    }
    public void setIsHostShow(Integer isHostShow) {
        this.isHostShow = isHostShow;
    }

    public Integer getIsHostShow() {
        return isHostShow;
    }
    public void setSortDesc(String sortDesc) {
        this.sortDesc = sortDesc;
    }

    public String getSortDesc() {
        return sortDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("isEffect", getIsEffect())
            .append("sort", getSort())
            .append("img", getImg())
            .append("classfy", getClassfy())
            .append("isHostShow", getIsHostShow())
            .append("sortDesc", getSortDesc())
            .toString();
    }
}
