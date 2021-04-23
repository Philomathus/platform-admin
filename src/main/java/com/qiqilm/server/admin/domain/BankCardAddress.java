package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 bank_card_address
 *
 * @author 77tv
 * @date 2021-04-21
 */
@Data
public class BankCardAddress extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String province;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String city;

    /** 0禁用1启用 */
    @Excel(name = "0禁用1启用")
    private String status;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("province", getProvince())
            .append("city", getCity())
            .append("status", getStatus())
            .append("createName", getCreateName())
            .toString();
    }
}