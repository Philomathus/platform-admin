package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 支付类型对象 pay_type
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class PayType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 编码 */
    @Excel(name = "编码")
    private Integer code;

    /** 图标 */
    @Excel(name = "图标")
    private String iconUrl;

    /** 排序 */
    @Excel(name = "排序")
    private Long indexes;

    /** 是否推荐(1是0否) */
    @Excel(name = "是否推荐(1是0否)")
    private String isRecommend;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 是否线上(1是0否) */
    @Excel(name = "是否线上(1是0否)")
    private Integer isOnline;

    /** 支付类型 1线上支付 2线下支付 3 代充支付 */
    @Excel(name = "支付类型 1线上支付 2线下支付 3 代充支付")
    private String type;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updator;
    /** 开放层级 */
    @Excel(name = "开放层级")
    private Long openLevel;

    @Excel(name = "文本1")
    private String tex1;
    @Excel(name = "文本2")
    private String tex2;
    @Excel(name = "文本3")
    private String tex3;
    @Excel(name = "文本4")
    private String tex4;
    @Excel(name = "文本5")
    private String tex5;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("code", getCode())
                .append("iconUrl", getIconUrl())
                .append("indexes", getIndexes())
                .append("isRecommend", getIsRecommend())
                .append("status", getStatus())
                .append("isOnline", getIsOnline())
                .append("type", getType())
                .append("creator", getCreator())
                .append("createTime", getCreateTime())
                .append("updator", getUpdator())
                .append("updateTime", getUpdateTime())
                .append("tex1", getTex1())
                .append("tex2", getTex2())
                .append("tex3", getTex3())
                .append("tex4", getTex4())
                .append("tex5", getTex5())
                .append("openLevel", getOpenLevel())
                .toString();
    }
}
