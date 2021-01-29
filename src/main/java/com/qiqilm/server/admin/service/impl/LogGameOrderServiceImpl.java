package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LogGameOrder;
import com.qiqilm.server.admin.mapper.LogGameOrderMapper;
import com.qiqilm.server.admin.service.ILogGameOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员上下分Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class LogGameOrderServiceImpl implements ILogGameOrderService {
    @Autowired
    private LogGameOrderMapper logGameOrderMapper;

    /**
     * 查询会员上下分
     *
     * @param id 会员上下分ID
     * @return 会员上下分
     */
    @Override
    public LogGameOrder selectLogGameOrderById(String id) {
        return logGameOrderMapper.selectLogGameOrderById(id);
    }

    /**
     * 查询会员上下分列表
     *
     * @param logGameOrder 会员上下分
     * @return 会员上下分
     */
    @Override
    public List<LogGameOrder> selectLogGameOrderList(LogGameOrder logGameOrder) {
        if (logGameOrder.getSelectDate() != null) {
            logGameOrder.setStartTime(logGameOrder.getSelectDate()[0] + " 00:00:00");
            logGameOrder.setEndTime(logGameOrder.getSelectDate()[1] + " 23:59:59");
        }
        return logGameOrderMapper.selectLogGameOrderList(logGameOrder);
    }

    /**
     * 新增会员上下分
     *
     * @param logGameOrder 会员上下分
     * @return 结果
     */
    @Override
    public int insertLogGameOrder(LogGameOrder logGameOrder) {
        return logGameOrderMapper.insertLogGameOrder(logGameOrder);
    }

    /**
     * 修改会员上下分
     *
     * @param logGameOrder 会员上下分
     * @return 结果
     */
    @Override
    public int updateLogGameOrder(LogGameOrder logGameOrder) {
        return logGameOrderMapper.updateLogGameOrder(logGameOrder);
    }

    /**
     * 批量删除会员上下分
     *
     * @param ids 需要删除的会员上下分ID
     * @return 结果
     */
    @Override
    public int deleteLogGameOrderByIds(String[] ids) {
        return logGameOrderMapper.deleteLogGameOrderByIds(ids);
    }

    /**
     * 删除会员上下分信息
     *
     * @param id 会员上下分ID
     * @return 结果
     */
    @Override
    public int deleteLogGameOrderById(String id) {
        return logGameOrderMapper.deleteLogGameOrderById(id);
    }
}
