package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
import com.qiqilm.server.admin.service.IPayTypeService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class PayTypeServiceImpl implements IPayTypeService {
    @Autowired
    private PayTypeMapper payTypeMapper;

    /**
     * 查询支付类型
     *
     * @param id 支付类型ID
     * @return 支付类型
     */
    @Override
    public PayType selectPayTypeById(String id) {
        return payTypeMapper.selectPayTypeById(id);
    }

    /**
     * 查询支付类型列表
     *
     * @param payType 支付类型
     * @return 支付类型
     */
    @Override
    public List<PayType> selectPayTypeList(PayType payType) {
        return payTypeMapper.selectPayTypeList(payType);
    }

    /**
     * 新增支付类型
     *
     * @param payType 支付类型
     * @return 结果
     */
    @Override
    public int insertPayType(PayType payType) {
        payType.setCreateTime(DateUtils.getNowDate());
        return payTypeMapper.insertPayType(payType);
    }

    /**
     * 修改支付类型
     *
     * @param payType 支付类型
     * @return 结果
     */
    @Override
    public int updatePayType(PayType payType) {
        payType.setUpdateTime(DateUtils.getNowDate());
        return payTypeMapper.updatePayType(payType);
    }

    /**
     * 批量删除支付类型
     *
     * @param ids 需要删除的支付类型ID
     * @return 结果
     */
    @Override
    public int deletePayTypeByIds(String[] ids) {
        return payTypeMapper.deletePayTypeByIds(ids);
    }

    /**
     * 删除支付类型信息
     *
     * @param id 支付类型ID
     * @return 结果
     */
    @Override
    public int deletePayTypeById(String id) {
        return payTypeMapper.deletePayTypeById(id);
    }
}
