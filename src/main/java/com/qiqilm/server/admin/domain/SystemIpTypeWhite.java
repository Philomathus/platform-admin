package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * IP黑名单或反作弊禁言对象 system_ip_type_white
 *
 * @author 77tv
 * @date 2021-07-12
 */
@Data
public class SystemIpTypeWhite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 参数 */
    @Excel(name = "参数")
    private String value;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 管理员 */
    @Excel(name = "管理员")
    private String opname;

    /** 备注 */
    @Excel(name = "备注")
    private String mark;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("value", getValue())
            .append("status", getStatus())
            .append("opname", getOpname())
            .append("mark", getMark())
            .toString();
    }
}
