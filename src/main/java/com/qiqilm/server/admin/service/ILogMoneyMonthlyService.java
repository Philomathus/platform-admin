package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;

import java.util.List;


public interface ILogMoneyMonthlyService {

    List<LogMoney> selectLogMoneyMonthlyList( LogMoney logMoney );

    AjaxResult listCount(LogMoney logMoney);

    AjaxResult totalCount(LogMoney logMoney);
}
