package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
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
    @Autowired
    private TokenService tokenService;

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
        return payChannelNewMapper.findList(payChannelNew);
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
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String        username  = loginUser.getUsername();
        payChannelNew.setCreator(username);
        payChannelNew.setStatus( "0" );
        payChannelNew.setFailNum( 0 );
        payChannelNew.setSuccessNum( 0 );
        payChannelNew.setTotalSuccessMoney( BigDecimal.ZERO );
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
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String        username  = loginUser.getUsername();
        payChannelNew.setUpdator( username );
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
