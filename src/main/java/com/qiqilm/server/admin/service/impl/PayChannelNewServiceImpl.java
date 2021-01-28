package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayChannelNewMapper;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.service.IPayChannelNewService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class PayChannelNewServiceImpl implements IPayChannelNewService {
    @Autowired
    private PayChannelNewMapper payChannelNewMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayChannelNew selectPayChannelNewById(Long id) {
        return payChannelNewMapper.selectPayChannelNewById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payChannelNew 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayChannelNew> selectPayChannelNewList(PayChannelNew payChannelNew) {
        return payChannelNewMapper.selectPayChannelNewList(payChannelNew);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payChannelNew 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayChannelNew(PayChannelNew payChannelNew) {
        payChannelNew.setCreateTime(DateUtils.getNowDate());
        return payChannelNewMapper.insertPayChannelNew(payChannelNew);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payChannelNew 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayChannelNew(PayChannelNew payChannelNew) {
        payChannelNew.setUpdateTime(DateUtils.getNowDate());
        return payChannelNewMapper.updatePayChannelNew(payChannelNew);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayChannelNewByIds(Long[] ids) {
        return payChannelNewMapper.deletePayChannelNewByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayChannelNewById(Long id) {
        return payChannelNewMapper.deletePayChannelNewById(id);
    }
}
