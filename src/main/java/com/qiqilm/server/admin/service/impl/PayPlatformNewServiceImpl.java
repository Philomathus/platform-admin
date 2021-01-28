package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayPlatformNewMapper;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.service.IPayPlatformNewService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class PayPlatformNewServiceImpl implements IPayPlatformNewService {
    @Autowired
    private PayPlatformNewMapper payPlatformNewMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayPlatformNew selectPayPlatformNewById(Long id) {
        return payPlatformNewMapper.selectPayPlatformNewById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payPlatformNew 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayPlatformNew> selectPayPlatformNewList(PayPlatformNew payPlatformNew) {
        return payPlatformNewMapper.selectPayPlatformNewList(payPlatformNew);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payPlatformNew 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayPlatformNew(PayPlatformNew payPlatformNew) {
        payPlatformNew.setCreateTime(DateUtils.getNowDate());
        return payPlatformNewMapper.insertPayPlatformNew(payPlatformNew);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payPlatformNew 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayPlatformNew(PayPlatformNew payPlatformNew) {
        payPlatformNew.setUpdateTime(DateUtils.getNowDate());
        return payPlatformNewMapper.updatePayPlatformNew(payPlatformNew);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayPlatformNewByIds(Long[] ids) {
        return payPlatformNewMapper.deletePayPlatformNewByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayPlatformNewById(Long id) {
        return payPlatformNewMapper.deletePayPlatformNewById(id);
    }
}
