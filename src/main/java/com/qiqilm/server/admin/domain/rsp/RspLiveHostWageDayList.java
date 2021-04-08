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

    @Excel( name = "主播昵称", orderNum = "2" )
    private String nickName;

    @Excel( name = "家族ID", orderNum = "2" )
    private Integer familyId;

    @Excel( name = "家族名称", orderNum = "3" )
    private String familyName;

    @Excel( name = "直播时长", orderNum = "4" )
    private String timeDes;

    @Excel( name = "时薪", orderNum = "5" )
    private String coin;

    @Excel( name = "时长结算", orderNum = "6" )
    private String livetimejiesuan;

    @Excel( name = "收礼金额", orderNum = "7" )
    private String ticket;

    @Excel( name = "礼物提成", orderNum = "8" )
    private BigDecimal liwujiesuanbili;

    @Excel( name = "礼物结算", orderNum = "9" )
    private BigDecimal ticketRes;

    @Excel( name = "彩票投注", orderNum = "10" )
    private BigDecimal lotteryBili;

    @Excel( name = "彩票提成", orderNum = "11" )
    private String costQianliu;

    @Excel( name = "彩票结算", orderNum = "12" )
    private BigDecimal lotteryCost;

    @Excel( name = "开播次数", orderNum = "13" )
    private Integer times;


//    @Excel( name = "统计日期", orderNum = "12" )
//    private String timedata;

    private int livetime;

    private BigDecimal allCpCost;

    private BigDecimal allPrize;

    private String shijian;

    private BigDecimal settlementRate;

    private Integer familyUserId;
    private String  familyNickName;

    public String getTimeDes() {
        if ( !StringUtils.isEmpty( livetime ) ) {
            double df = livetime;
            return new Formatter().format( "%.2f", df / 3600 ).toString();
        }
        return "";
    }

    public BigDecimal getTicketRes() {
        if ( ticketRes != null ) {
            return ticketRes.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}
