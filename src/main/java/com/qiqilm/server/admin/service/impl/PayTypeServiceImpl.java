package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
import com.qiqilm.server.admin.service.IPayTypeService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class PayTypeServiceImpl implements IPayTypeService {
    @Autowired
    private PayTypeMapper payTypeMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public PayType selectPayTypeById(String id) {
        return payTypeMapper.selectPayTypeById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payType 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<PayType> selectPayTypeList(PayType payType) {
        return payTypeMapper.selectPayTypeList(payType);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param payType 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPayType(PayType payType) {
        payType.setCreateTime(DateUtils.getNowDate());
        return payTypeMapper.insertPayType(payType);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param payType 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePayType(PayType payType) {
        payType.setUpdateTime(DateUtils.getNowDate());
        return payTypeMapper.updatePayType(payType);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayTypeByIds(String[] ids) {
        return payTypeMapper.deletePayTypeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deletePayTypeById(String id) {
        return payTypeMapper.deletePayTypeById(id);
    }
}
