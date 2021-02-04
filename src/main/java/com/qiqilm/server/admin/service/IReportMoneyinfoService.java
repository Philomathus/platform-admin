package com.qiqilm.server.admin.service;


import com.qiqilm.server.admin.domain.ReportMoneyinfo;

import java.util.List;

/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IReportMoneyinfoService {


    /**
     * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
     *
     * @param reportMoneyinfo 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
     * @return 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额集合
     */
    public List<ReportMoneyinfo> selectReportMoneyinfoList(ReportMoneyinfo reportMoneyinfo);


    ReportMoneyinfo countMoneyData(ReportMoneyinfo reportMoneyinfo);

    Object storage(ReportMoneyinfo reportMoneyinfo);
}