package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPayJourRsp {
    private String id;

    private String memberId;

    private BigDecimal money;

    private String platformName;

    private String channelName;

    private String status;

    private String orderTime;

    private String platformId;

    private String channelId;
}
