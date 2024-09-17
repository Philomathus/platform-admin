package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.service.ILogMoneyService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员资金信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class LogMoneyServiceImpl implements ILogMoneyService {
    @Autowired
    private LogMoneyMapper logMoneyMapper;

    /**
     * 查询 会员资金信息列表
     *
     * @param logMoney 会员资金信息
     *
     * @return 会员资金信息
     */
    @Override
    public List<LogMoney> selectLogMoneyList( LogMoney logMoney ) {
        this.getTime( logMoney );
        if ( StringUtils.isNotBlank( logMoney.getSearchValue() ) && logMoney.getSearchValue().startsWith( "77" ) ) {
            String tableLast = logMoney.getSearchValue().substring( logMoney.getSearchValue().length() - 1 );
            logMoney.setTableLast( tableLast );
            return logMoneyMapper.selectLogMoneySingleList( logMoney );
        } else {
            return logMoneyMapper.selectLogMoneyList( logMoney );
        }
    }

    @Override
    public AjaxResult listCount( LogMoney logMoney ) {
        this.getTime( logMoney );
        if ( StringUtils.isNotBlank( logMoney.getSearchValue() ) && logMoney.getSearchValue().startsWith( "77" ) ) {
            String tableLast = logMoney.getSearchValue().substring( logMoney.getSearchValue().length() - 1 );
            logMoney.setTableLast( tableLast );
            return AjaxResult.success( logMoneyMapper.listCount( logMoney ) );
        }
        return AjaxResult.success( logMoneyMapper.listCountAll( logMoney ) );
    }


    private void getTime( LogMoney logMoney ) {
        if ( logMoney.getSelectDate() != null && logMoney.getSelectDate().length > 0 ) {
            logMoney.setStartTime( logMoney.getSelectDate()[ 0 ] );
            logMoney.setEndTime( logMoney.getSelectDate()[ 1 ] );
        }
    }
}
