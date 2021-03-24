package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 主播提现管理对象 live_user_withdraw_newlog
 *
 * @author 77tv
 * @date 2021-03-23
 */
@Data
public class LiveUserWithdrawNewlog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long userId;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String nickName;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 订单表达式 */
    @Excel(name = "订单表达式")
    private String orderExpression;

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

    private String[] selectDate;

    /** 提现银行类型ID */
    @Excel(name = "提现银行类型ID")
    private Long bankTypeId;

    /** 状态(0申请中1初级审核通过2审核不通过3终极审核通过) */
    @Excel(name = "状态(0申请中1初级审核通过2审核不通过3终极审核通过)")
    private Long wstatus;

    /** 提现类型(1提现到银行卡,2提现到支付宝) */
    @Excel(name = "提现类型(1提现到银行卡,2提现到支付宝)")
    private Long type;

    /** 审核员 */
    @Excel(name = "审核员")
    private String opName;

    /** 主播时长 */
    @Excel(name = "主播时长")
    private BigDecimal livetime;

    /** 主播礼物 */
    @Excel(name = "主播礼物")
    private BigDecimal liveticket;

    /** 主播派奖 */
    @Excel(name = "主播派奖")
    private BigDecimal livepaijiang;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("nickName", getNickName())
            .append("orderNo", getOrderNo())
            .append("orderExpression", getOrderExpression())
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
