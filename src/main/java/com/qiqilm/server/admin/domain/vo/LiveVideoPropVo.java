package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播间消费日志
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LiveVideoPropVo {

    private String id;

    private String prop_id;

    private String prop_name;

    private BigDecimal total_diamonds;

    private BigDecimal current_diamonds;

    private Integer to_user_id;

    private String p_user_id;

    private Long create_time;

}
