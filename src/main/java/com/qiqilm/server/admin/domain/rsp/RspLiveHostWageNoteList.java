package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Formatter;

@Data
public class RspLiveHostWageNoteList {
	@Excel( name = "家族ID", orderNum = "0" )
	private Integer familyId;

	@Excel( name = "家族名称", orderNum = "1" )
	private String familyName;

	@Excel( name = "主播ID", orderNum = "2" )
	private Integer hostId;

	@Excel( name = "主播昵称", orderNum = "3" )
	private String nickName;

	@Excel( name = "直播总时长（小时）", orderNum = "4" )
	private String alltimeDes;

	@Excel( name = "主播直播总结算印票", orderNum = "5" )
	private String allticket;

	@Excel( name = "主播直播结算印票", orderNum = "6" )
	private BigDecimal allticketRes;

	@Excel( name = "统计日期", orderNum = "7" )
	private String timedata;

	private int alltime;

	private BigDecimal allCpCost;

	private BigDecimal allPrize;

	private String shijian;

	private BigDecimal settlementRate;

	private Integer familyUserId;
	private String  familyNickName;

	public String getAlltimeDes() {
		if ( !StringUtils.isEmpty( alltime ) ) {
			double df = alltime;
			return new Formatter().format( "%.2f", df / 3600 ).toString();
		}
		return "";
	}

}