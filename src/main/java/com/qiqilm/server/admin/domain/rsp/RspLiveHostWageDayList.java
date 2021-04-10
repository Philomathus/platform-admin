package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Formatter;

@Data
public class RspLiveHostWageDayList {
    @Excel( name = "主播ID", orderNum = "0" )
    private Integer hostId;

    @Excel( name = "主播昵称", orderNum = "1" )
    private String nickName;

    @Excel( name = "家族ID", orderNum = "2" )
    private Integer familyId;

    @Excel( name = "家族名称", orderNum = "3" )
    private String familyName;

    @Excel( name = "直播时长", orderNum = "4" )
    private String livetime;

    @Excel( name = "礼物任务", orderNum = "5" )
    private String lwrenwu;

    @Excel( name = "时长任务", orderNum = "6" )
    private String screnwu;

    @Excel( name = "时薪", orderNum = "7" )
    private String coin;

    @Excel( name = "时长结算", orderNum = "8" )
    private String livetimejiesuan;

    @Excel( name = "收礼金额", orderNum = "9" )
    private String liwu;

    @Excel( name = "礼物提成", orderNum = "10" )
    private BigDecimal liwujiesuanbili;

    @Excel( name = "礼物结算", orderNum = "11" )
    private BigDecimal ticket;

    @Excel( name = "彩票投注", orderNum = "12" )
    private BigDecimal lotteryBili;

    @Excel( name = "彩票提成", orderNum = "13" )
    private String costQianliu;

    @Excel( name = "彩票结算", orderNum = "14" )
    private BigDecimal lotteryCost;

    @Excel( name = "开播次数", orderNum = "15" )
    private Integer times;

//    private int livetime;

    private BigDecimal allCpCost;

    private BigDecimal allPrize;

    private String shijian;

    private BigDecimal settlementRate;

    private Integer familyUserId;
    private String  familyNickName;

//    public String getTimeDes() {
//        if ( !StringUtils.isEmpty( livetime ) ) {
//            double df = livetime;
//            return new Formatter().format( "%.2f", df / 3600 ).toString();
//        }
//        return "";
//    }

    public BigDecimal getTicket() {
        if ( ticket != null ) {
            return ticket.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}
