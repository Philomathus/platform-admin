package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ReportMoneyinfoMapper {


	/**
	 * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
	 *
	 * @param reportMoneyinfo 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
	 * @return 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额集合
	 */
	public List<ReportMoneyinfo> selectReportMoneyinfoList(ReportMoneyinfo reportMoneyinfo);

	String calldataProrepPlamcom(@Param( "statimedateta" ) String statimedateta, @Param( "endtimedateta" ) String endtimedateta);


	ReportMoneyinfo countMoneyInfoData(ReportMoneyinfo reportMoneyinfo);
}