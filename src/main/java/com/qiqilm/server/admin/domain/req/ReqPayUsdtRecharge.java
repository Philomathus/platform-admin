package com.qiqilm.server.admin.domain.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqPayUsdtRecharge extends BaseEntity {

    private Long id;
    private String memberId;
    private String userName;
    private String channelName;
    private Long rechargeNumber;
    private BigDecimal rechargeMoney;
    private Integer status;
    private BigDecimal discountBill;
    private String chainName;
    private String rechargeAddress;
    private String transactionId;
    private String opName;
    private String searchValue;

    @JsonIgnore
    private String[] selectDate;
    @JsonIgnore
    private String   selectStartDate;
    @JsonIgnore
    private String   selectEndDate;
}
