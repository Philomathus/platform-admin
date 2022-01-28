package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 【请填写功能名称】对象 live_host_wage_day
 *
 * @author 77tv
 * @date 2021-03-29
 */
@Data
public class RspLiveHostWageDays {
    @Excel( name = "主播ID", orderNum = "1" )
    private Integer hostId;

    @Excel( name = "主播昵称", orderNum = "2" )
    private String nickName;

    @Excel( name = "家族名称", orderNum = "3" )
    private String familyName;

    @Excel( name = "开播时长", orderNum = "4" )
    private String livetime;

    @Excel( name = "时长结算", orderNum = "5" )
    private String livetimejiesuan;

    @Excel( name = "礼物结算", orderNum = "6" )
    private BigDecimal ticket;

    @Excel( name = "彩票结算", orderNum = "7" )
    private BigDecimal costQianliu;

    @Excel( name = "开播次数", orderNum = "8" )
    private Integer times;

    @Excel( name = "总收入", orderNum = "9" )
    private BigDecimal totalsettle;


    public BigDecimal getTicket() {
        if ( ticket != null ) {
            return ticket.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}