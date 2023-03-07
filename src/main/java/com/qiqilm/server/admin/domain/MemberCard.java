package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_card
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberCard extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    private String id;

    /**
     * 姓名
     */
    @Excel( name = "姓名" )
    private String realName;

    /**
     * 银行名称
     */
    @Excel( name = "银行名称" )
    private String bankName;

    /**
     * 银行编码
     */
    @Excel( name = "银行编码" )
    private String bankCode;

    /**
     * 银行账号
     */
    @Excel( name = "银行账号" )
    private String bankAccount;

    /**
     * 银行地址
     */
    @Excel( name = "银行地址" )
    private String bankAddress;

    /**
     * 会员编号
     */
    @Excel( name = "会员编号" )
    private String memberId;

    /**
     * 卡片类型1=银行卡2=支付宝
     */
    @Excel( name = "卡片类型1=银行卡2=支付宝" )
    private Long type;

    /**
     * 是否默认
     */
    @Excel( name = "是否默认" )
    private Integer dv;
    @Excel( name = "银行卡真实归属地" )
    private String  realBankAddress;

    @Excel( name = "银行卡ID" )
    private Integer bankId;

    @Override
    public String toString() {
        return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE ).append( "id", getId() )
                                                                          .append( "realName", getRealName() )
                                                                          .append( "bankName", getBankName() )
                                                                          .append( "bankCode", getBankCode() )
                                                                          .append( "bankAccount", getBankAccount() )
                                                                          .append( "bankAddress", getBankAddress() )
                                                                          .append( "memberId", getMemberId() )
                                                                          .append( "createTime", getCreateTime() )
                                                                          .append( "type", getType() ).append( "dv", getDv() )
                                                                          .append( "realBankAddress", getRealBankAddress() )
                                                                          .toString();
    }
}
