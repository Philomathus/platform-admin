package com.qiqilm.server.admin.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播间消费日志
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LiveVideoPropVo {
    @ApiModelProperty(name = "id")
    private String id;
    @ApiModelProperty(name = "类型id")
    private String prop_id;
    @ApiModelProperty(name = "名称")
    private String prop_name;
    @ApiModelProperty(name = "会员减少的资金，增加为负数")
    private BigDecimal total_diamonds;
    @ApiModelProperty(name = "变动后会员资金")
    private BigDecimal current_diamonds;
    @ApiModelProperty(value = "送礼=主播ID,其他为-1")
    private Integer to_user_id;
    @ApiModelProperty(value = "会员ID")
    private String p_user_id;
    @ApiModelProperty(value = "时间秒")
    private Integer create_time;

}
