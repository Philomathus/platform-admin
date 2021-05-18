package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 出款银行列表对象 bank_list
 *
 * @author 77tv
 * @date 2021-04-06
 */
@Data
public class BankList extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 银行英文编码 */
    @Excel(name = "银行英文编码")
    private String bankCode;

    /** 银行图标地址 */
    @Excel(name = "银行图标地址")
    private String bankIcon;

    /** 状态 1激活 0隐藏 */
    @Excel(name = "状态 1激活 0隐藏")
    private String status;

    /** 排序 从小到大顺序 */
    @Excel(name = "排序 从小到大顺序")
    private Long sort;

    /** 官网地址 */
    @Excel(name = "官网地址")
    private String url;

    /** 结束颜色 */
    @Excel(name = "结束颜色")
    private String colorEnd;

    /** 开始颜色 */
    @Excel(name = "开始颜色")
    private String colorStart;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("bankName", getBankName())
            .append("bankCode", getBankCode())
            .append("bankIcon", getBankIcon())
            .append("status", getStatus())
            .append("sort", getSort())
            .append("url", getUrl())
            .append("colorEnd", getColorEnd())
            .append("colorStart", getColorStart())
            .toString();
    }
}
