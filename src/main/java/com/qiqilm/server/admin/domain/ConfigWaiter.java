package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客服管理对象 config_waiter
 *
 * @author 77tv
 * @date 2021-03-03
 */
@Data
public class ConfigWaiter extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 类型(1qq2微信) */
    @Excel(name = "类型(1qq2微信)")
    private Long type;

    /** 微信或者QQ号等 */
    @Excel(name = "微信或者QQ号等")
    private String code;

    /** 昵称 */
    @Excel(name = "昵称")
    private String name;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Long indexs;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("code", getCode())
            .append("name", getName())
            .append("icon", getIcon())
            .append("status", getStatus())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("indexs", getIndexs())
            .append("remark", getRemark())
            .toString();
    }
}
