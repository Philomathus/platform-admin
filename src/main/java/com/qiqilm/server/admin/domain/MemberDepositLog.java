package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 人工加分日志对象 member_deposit_log
 *
 * @author 77tv
 * @date 2021-07-29
 */
@Data
public class MemberDepositLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private Long id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 加分金额 */
    @Excel(name = "加分金额")
    private BigDecimal money;

    /** 支付备注 */
    @Excel(name = "支付备注")
    private String remarkPay;

    /** 订单备注 */
    @Excel(name = "订单备注")
    private String orderRemark;

    /** 打码倍数 */
    @Excel(name = "打码倍数")
    private Integer beatNum;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date opTime;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

    /** 提交IP */
    @Excel(name = "提交IP")
    private String ip;

    /** 入款类型(1人工入款 2线上入款 3线下入款) */
    @Excel(name = "入款类型(1人工入款 2线上入款 3线下入款)")
    private String moneydes;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("userName", getUserName())
            .append("money", getMoney())
            .append("remark", getRemark())
            .append("remarkPay", getRemarkPay())
            .append("orderRemark", getOrderRemark())
            .append("beatNum", getBeatNum())
            .append("opTime", getOpTime())
            .append("opName", getOpName())
            .append("ip", getIp())
            .append("moneydes", getMoneydes())
            .toString();
    }
}
