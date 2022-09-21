package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 主播提现管理对象 live_user_withdraw_newlog
 *
 * @author 77tv
 * @date 2021-04-08
 */
@Data
public class LiveUserWithdrawNewlog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long userId;

    /** 家族ID */
    @Excel(name = "家族ID")
    private Long familyId;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String nickName;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal withdrawMoney;

    /** 提现收款人真实姓名 */
    @Excel(name = "提现收款人真实姓名")
    private String bankUserName;

    /** 提现银行账号 */
    @Excel(name = "提现银行账号")
    private String bankAccount;

    /** 提现银行账号开户行 */
    @Excel(name = "提现银行账号开户行")
    private String bankAddress;

    /** 提现银行类型ID */
    @Excel(name = "提现银行类型ID")
    private Long bankTypeId;

    /** 状态(0申请中1初级审核通过2审核不通过3终极审核通过) */
    @Excel(name = "状态(0申请中1提交申请2审核不通过3终极审核通过,4出款中,5出款成功)")
    private Long wstatus;

    /** 提现类型(1提现到银行卡,2提现到支付宝) */
    @Excel(name = "提现类型(1家族,2个人)")
    private Long type;

    /** 审核员 */
    @Excel(name = "审核员")
    private String opName;

    /** 主播时长 */
    @Excel(name = "底薪结算")
    private BigDecimal livetime;

    /** 主播礼物 */
    @Excel(name = "礼物结算")
    private BigDecimal liveticket;

    /** 主播派奖 */
    @Excel(name = "彩票结算")
    private BigDecimal livepaijiang;

    /**
     * 银行卡省/市
     */
    @Excel(name = "银行卡省/市")
    private String realBankAddress;

    /**
     * 银行卡黑名单1是0否
     */
    @Excel(name = "银行卡黑名单")
    private String cardBlack = "0";

    private String SearchCardBlack;
    private String province;
    private String city;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;

    @JsonIgnore
    private String[] searchTime;
    @JsonIgnore
    private String   startTime;
    @JsonIgnore
    private String   endTime;
    private Integer googleAuthCode;


    public String getStartTime() {
        if ( searchTime != null && searchTime.length > 0 ) {
            return searchTime[ 0 ];
        }
        return null;
    }

    public String getEndTime() {
        if ( searchTime != null && searchTime.length > 0 ) {
            return searchTime[ 1 ];
        }
        return null;
    }




    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("familyId", getFamilyId())
            .append("nickName", getNickName())
            .append("orderNo", getOrderNo())
            .append("withdrawMoney", getWithdrawMoney())
            .append("bankUserName", getBankUserName())
            .append("bankAccount", getBankAccount())
            .append("bankAddress", getBankAddress())
            .append("bankTypeId", getBankTypeId())
            .append("wstatus", getWstatus())
            .append("type", getType())
            .append("opName", getOpName())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("livetime", getLivetime())
            .append("liveticket", getLiveticket())
            .append("livepaijiang", getLivepaijiang())
            .toString();
    }
}
