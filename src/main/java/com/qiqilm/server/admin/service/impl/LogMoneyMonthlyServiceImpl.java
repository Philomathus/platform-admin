package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.mapper.LogMoneyMonthlyMapper;
import com.qiqilm.server.admin.service.ILogMoneyMonthlyService;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LogMoneyMonthlyServiceImpl implements ILogMoneyMonthlyService {

    @Autowired
    LogMoneyMonthlyMapper logMoneyMonthlyMapper;

    @Override
    public List<LogMoney> selectLogMoneyMonthlyList( LogMoney logMoney ){
        try {
            return logMoneyMonthlyMapper.selectLogMonthlyList(logMoney);
        }catch (Exception e){
            log.error( "数据库仍然不存在", e );
            return new ArrayList<>();
        }
    }

    @Override
    public AjaxResult totalCount(LogMoney logMoney ) {
        try {
            return AjaxResult.success( logMoneyMonthlyMapper.totalMonthlyCount( logMoney ) );
        }catch( Exception e ){
            log.error( "数据库仍然不存在", e );
            return AjaxResult.error( "数据库仍然不存在" );
        }
    }

    @Override
    public AjaxResult listCount( LogMoney logMoney ) {
        if ( StringUtils.isNotBlank( logMoney.getSearchValue() ) && logMoney.getSearchValue().startsWith( "77" ) ) {
            String tableLast = logMoney.getSearchValue().substring( logMoney.getSearchValue().length() - 1 );
            logMoney.setTableLast( tableLast );
        }
        try {
            return AjaxResult.success( logMoneyMonthlyMapper.listMonthlyCount( logMoney ) );
        }catch( Exception e ){
            log.error( "数据库仍然不存在", e );
            return AjaxResult.error( "数据库仍然不存在" );
        }

    }
}
