package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.service.ILogMoneyService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return 会员资金信息
     */
    @Override
    public List<LogMoney> selectLogMoneyList(LogMoney logMoney) {
        LogMoney logMoney1 = getTime(logMoney);
        String tableLast;
        if (StringUtils.isNotBlank(logMoney1.getSearchValue()) && logMoney1.getSearchValue().startsWith("77")) {
            tableLast = logMoney1.getSearchValue().substring(logMoney1.getSearchValue().length() - 1);
        } else {
            tableLast = "_view";
        }
        logMoney1.setTableLast(tableLast);
        return logMoneyMapper.selectLogMoneyList(logMoney1);
    }

    @Override
    public AjaxResult totalCount(LogMoney logMoney) {
        LogMoney logMoney1 = getTime(logMoney);
        return AjaxResult.success(logMoneyMapper.totalCount(logMoney1));
    }


    private LogMoney getTime(LogMoney logMoney) {
        if (logMoney.getSelectDate() != null && logMoney.getSelectDate().length > 0) {
            logMoney.setStartTime(logMoney.getSelectDate()[0]);
            logMoney.setEndTime(logMoney.getSelectDate()[1]);
        }
        return logMoney;
    }
}
