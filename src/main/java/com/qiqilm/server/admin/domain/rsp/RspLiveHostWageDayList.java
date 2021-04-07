package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Formatter;

@Data
public class RspLiveHostWageDayList {
    @Excel( name = "家族ID", orderNum = "0" )
    private Integer familyId;

    @Excel( name = "家族名称", orderNum = "1" )
    private String familyName;

    @Excel( name = "主播ID", orderNum = "2" )
    private Integer hostId;

    @Excel( name = "主播昵称", orderNum = "3" )
    private String nickName;

    @Excel( name = "直播总时长（小时）", orderNum = "4" )
    private String timeDes;

    @Excel( name = "主播结算印票", orderNum = "5" )
    private String ticket;

    @Excel( name = "主播折扣结算印票", orderNum = "6" )
    private BigDecimal ticketRes;

    @Excel( name = "彩票投注", orderNum = "7" )
    private BigDecimal lotteryCost;

    @Excel( name = "上播次数", orderNum = "8" )
    private Integer times;

    @Excel( name = "派奖千六", orderNum = "9" )
    private Integer costQianliu;

    @Excel( name = "彩票比例", orderNum = "10" )
    private Integer lotteryBili;

    @Excel( name = "礼物结算比例", orderNum = "11" )
    private Integer liwujiesuanbili;

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
