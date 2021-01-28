package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayLogMapper;
import com.qiqilm.server.admin.domain.PayLog;
import com.qiqilm.server.admin.service.IPayLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayLogServiceImpl implements IPayLogService {
    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayLog selectPayLogById(Long id) {
        return payLogMapper.selectPayLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayLog> selectPayLogList(PayLog payLog) {
        return payLogMapper.selectPayLogList(payLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayLog(PayLog payLog) {
        payLog.setCreateTime(DateUtils.getNowDate());
        return payLogMapper.insertPayLog(payLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayLog(PayLog payLog) {
        return payLogMapper.updatePayLog(payLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayLogByIds(Long[] ids) {
        return payLogMapper.deletePayLogByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayLogById(Long id) {
        return payLogMapper.deletePayLogById(id);
    }
}
