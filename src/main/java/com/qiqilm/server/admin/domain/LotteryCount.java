package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;


/**
 * lottery Count
 *
 * @author rajesh
 * @date 2022-09-30
 */

@Data
public class LotteryCount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String agent;

    private Long lotteryId;

    private String pUserId;

    private String issue;

    private String betInfo;

    private String chip;

    private String ip;

    private String lotteryName;
}
