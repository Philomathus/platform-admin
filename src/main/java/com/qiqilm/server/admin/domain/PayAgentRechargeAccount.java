package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_account
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class PayAgentRechargeAccount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 代充账号 */
    @Excel(name = "代充账号")
    private String account;

    /** 代充昵称 */
    @Excel(name = "代充昵称")
    private String nickName;

    /** 当前余额额度 */
    @Excel(name = "当前余额额度")
    private BigDecimal balanceAmount;

    /** 代充次数 */
    @Excel(name = "代充次数")
    private Long rechargeNum;

    /** QQ号 */
    @Excel(name = "QQ号")
    private String qqAccount;

    /** 微信号或微信注册手机号 */
    @Excel(name = "微信号或微信注册手机号")
    private String wechatAccount;

    /** 支付宝账号或支付宝注册手机号 */
    @Excel(name = "支付宝账号或支付宝注册手机号")
    private String alipayAccount;

    /** 手机号 */
    @Excel(name = "手机号")
    private String mobile;

    /** 开店时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开店时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date businessBeginTime;

    /** 关店时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "关店时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date businessEndTime;

    /** 充值优惠比例 */
    @Excel(name = "充值优惠比例")
    private BigDecimal rechargeDiscountRate;

    /** 状态 1正常 0拉黑 */
    @Excel(name = "状态 1正常 0拉黑")
    private String status;

    /** 上次登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上次登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date beforeLoginTime;

    /** 本次登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "本次登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date loginTime;



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("account", getAccount())
            .append("nickName", getNickName())
            .append("balanceAmount", getBalanceAmount())
            .append("rechargeNum", getRechargeNum())
            .append("qqAccount", getQqAccount())
            .append("wechatAccount", getWechatAccount())
            .append("alipayAccount", getAlipayAccount())
            .append("mobile", getMobile())
            .append("businessBeginTime", getBusinessBeginTime())
            .append("businessEndTime", getBusinessEndTime())
            .append("rechargeDiscountRate", getRechargeDiscountRate())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("beforeLoginTime", getBeforeLoginTime())
            .append("loginTime", getLoginTime())
            .toString();
    }
}
