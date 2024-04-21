package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ActivityWithdrawCashBack;
import com.qiqilm.server.admin.domain.ConfigBank;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface ActivityWithdrawCashBackMapper {
    List<ActivityWithdrawCashBack> list( ActivityWithdrawCashBack req );

    List<ConfigBank> getConfigBankList();

    ActivityWithdrawCashBack selectById( Integer id );

    int deleteByIds( List<String> bankCodes );

    int update( ActivityWithdrawCashBack req );

    int add( ActivityWithdrawCashBack req );

    int updateStatus( ActivityWithdrawCashBack req );
}
