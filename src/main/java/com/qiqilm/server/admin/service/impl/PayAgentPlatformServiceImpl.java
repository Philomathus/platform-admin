package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentPlatformMapper;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.service.IPayAgentPlatformService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentPlatformServiceImpl implements IPayAgentPlatformService {
    @Autowired
    private PayAgentPlatformMapper payAgentPlatformMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayAgentPlatform selectPayAgentPlatformById(Long id) {
        return payAgentPlatformMapper.selectPayAgentPlatformById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentPlatform 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayAgentPlatform> selectPayAgentPlatformList(PayAgentPlatform payAgentPlatform) {
        return payAgentPlatformMapper.selectPayAgentPlatformList(payAgentPlatform);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentPlatform 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayAgentPlatform(PayAgentPlatform payAgentPlatform) {
        return payAgentPlatformMapper.insertPayAgentPlatform(payAgentPlatform);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentPlatform 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayAgentPlatform(PayAgentPlatform payAgentPlatform) {
        return payAgentPlatformMapper.updatePayAgentPlatform(payAgentPlatform);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentPlatformByIds(Long[] ids) {
        return payAgentPlatformMapper.deletePayAgentPlatformByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayAgentPlatformById(Long id) {
        return payAgentPlatformMapper.deletePayAgentPlatformById(id);
    }
}
