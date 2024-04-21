package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.ActivityWithdrawCashBack;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.mapper.ActivityWithdrawCashBackMapper;
import com.qiqilm.server.admin.service.IActivityWithdrawCashBackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ActivityWithdrawCashBackServiceImpl implements IActivityWithdrawCashBackService {
    @Resource
    ActivityWithdrawCashBackMapper mapper;

    @Override
    public ActivityWithdrawCashBack selectById( Integer id ) {
        return mapper.selectById( id );
    }

    @Override
    public List<ActivityWithdrawCashBack> list( ActivityWithdrawCashBack req ) {
        return mapper.list( req );
    }

    @Override
    public int deleteByBankCodes( List<String> bankCodes ) {
        return mapper.deleteByIds( bankCodes );
    }

    @Override
    public int add( ActivityWithdrawCashBack req ) {
        req.setStatus( 0 );
        return mapper.add( req );
    }

    @Override
    public int update( ActivityWithdrawCashBack req ) {
        req.setStatus( null );
        return mapper.update( req );
    }

    @Override
    public int updateStatus( ActivityWithdrawCashBack req ) {
        return mapper.updateStatus( req );
    }

    @Override
    public List<ConfigBank> getConfigBankList() {
        return mapper.getConfigBankList();
    }
}
