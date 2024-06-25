package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LogMoney;

import java.util.List;
import java.util.Map;

public interface LogMoneyMonthlyMapper {

    List<LogMoney> selectLogMonthlyList( LogMoney logMoney );
    Map totalMonthlyCount(LogMoney logMoney );
    Map listMonthlyCount( LogMoney logMoney );
}
