package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.service.ILogMoneyService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LogMoneyServiceImpl implements ILogMoneyService {
    @Autowired
    private LogMoneyMapper logMoneyMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LogMoney selectLogMoneyById(String id) {
        return logMoneyMapper.selectLogMoneyById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param logMoney 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LogMoney> selectLogMoneyList(LogMoney logMoney) {
        return logMoneyMapper.selectLogMoneyList(logMoney);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param logMoney 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLogMoney(LogMoney logMoney) {
        logMoney.setCreateTime(DateUtils.getNowDate());
        return logMoneyMapper.insertLogMoney(logMoney);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param logMoney 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLogMoney(LogMoney logMoney) {
        return logMoneyMapper.updateLogMoney(logMoney);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLogMoneyByIds(String[] ids) {
        return logMoneyMapper.deleteLogMoneyByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLogMoneyById(String id) {
        return logMoneyMapper.deleteLogMoneyById(id);
    }
}
