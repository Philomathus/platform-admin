package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.ActivityWithdrawCashBack;
import com.qiqilm.server.admin.domain.ConfigBank;

import java.util.List;

public interface IActivityWithdrawCashBackService {
    ActivityWithdrawCashBack selectById( Integer id );

    List<ActivityWithdrawCashBack> list( ActivityWithdrawCashBack req );

    int deleteByBankCodes( List<String> bankCodes );

    int add( ActivityWithdrawCashBack req );

    int update( ActivityWithdrawCashBack req );

    int updateStatus( ActivityWithdrawCashBack req );

    List<ConfigBank> getConfigBankList();
}