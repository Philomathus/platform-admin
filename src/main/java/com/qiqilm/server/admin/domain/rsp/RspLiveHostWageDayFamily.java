package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Formatter;

@Data
public class RspLiveHostWageDayFamily {
    @Excel( name = "家族ID", orderNum = "0" )
    private Integer familyId;

    @Excel( name = "家族名称", orderNum = "1" )
    private String familyName;

    @Excel( name = "族长ID", orderNum = "2" )
    private Integer familyUserId;

    @Excel( name = "族长昵称", orderNum = "3" )
    private String familyNickName;

    @Excel( name = "直播时长", orderNum = "4" )
    private String livetime;

    @Excel( name = "时长结算", orderNum = "5" )
    private String livetimejiesuan;

    @Excel( name = "礼物金额", orderNum = "6" )
    private String liwu;

    @Excel( name = "礼物结算", orderNum = "7" )
    private BigDecimal liwujiesuan;

    @Excel( name = "彩票投注", orderNum = "8" )
    private BigDecimal lotteryCost;

    @Excel( name = "彩票结算", orderNum = "9" )
    private String costQianliu;

    @Excel( name = "开播次数", orderNum = "10" )
    private Integer times;

    private BigDecimal allCpCost;

    private BigDecimal allPrize;

    private String shijian;

    private BigDecimal settlementRate;

    public BigDecimal getAllticketRes() {
        if ( liwujiesuan != null ) {
            return liwujiesuan.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}
