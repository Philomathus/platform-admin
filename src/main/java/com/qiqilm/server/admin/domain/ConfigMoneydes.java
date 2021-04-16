package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_moneydes
 *
 * @author 77tv
 * @date 2021-01-29
 */
public class ConfigMoneydes extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long mdId;

    /** val值 */
    @Excel(name = "val值")
    private String val;

    /** 说明 */
    @Excel(name = "说明")
    private String des;

    public void setMdId(Long mdId) {
        this.mdId = mdId;
    }

    public Long getMdId() {
        return mdId;
    }
    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
    public void setDes(String des) {
        this.des = des;
    }

    public String getDes() {
        return des;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("mdId", getMdId())
            .append("val", getVal())
            .append("des", getDes())
            .toString();
    }
}
