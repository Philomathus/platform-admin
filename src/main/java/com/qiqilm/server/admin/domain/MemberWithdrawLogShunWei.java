package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

@Data
public class MemberWithdrawLogShunWei {
    private static final long serialVersionUID = 1L;

    /**
     * 提现银行
     */
    @Excel( name = "银行名称", orderNum = "1" )
    private String bankName;

    /**
     * 收款人
     */
    @Excel( name = "持卡人姓名", orderNum = "2" )
    private String bankUserName;

    /**
     * 提现账号
     */
    @Excel( name = "银行卡号", orderNum = "3" )
    private String bankAccount;

    /**
     * 提现金额
     */
    @Excel( name = "提现金额", orderNum = "4" )
    private Integer withdrawMoney;

    @Override
    public String toString() {
        return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
                .append( "withdrawMoney", getWithdrawMoney() )
                .append( "bankName", getBankName() )
                .append( "bankAccount", getBankAccount() )
                .append( "bankUserName", getBankUserName() )
                .toString();
    }

}
