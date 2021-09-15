package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayUsdtRechargeMapper;
import com.qiqilm.server.admin.domain.PayUsdtRecharge;
import com.qiqilm.server.admin.service.IPayUsdtRechargeService;

/**
 * USDT充值提交记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-14
 */
@Service
public class PayUsdtRechargeServiceImpl implements IPayUsdtRechargeService {
    @Autowired
    private PayUsdtRechargeMapper payUsdtRechargeMapper;
    @Autowired
    private TokenService       tokenService;

    /**
     * 查询USDT充值提交记录
     *
     * @param id USDT充值提交记录ID
     * @return USDT充值提交记录
     */
    @Override
    public PayUsdtRecharge selectPayUsdtRechargeById(Long id) {
        return payUsdtRechargeMapper.selectPayUsdtRechargeById(id);
    }

    /**
     * 查询USDT充值提交记录列表
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return USDT充值提交记录
     */
    @Override
    public List<PayUsdtRecharge> selectPayUsdtRechargeList(PayUsdtRecharge payUsdtRecharge) {
        String[] selectDate = payUsdtRecharge.getSelectDate();
        if ( selectDate != null && selectDate.length > 0 ) {
            payUsdtRecharge.setSelectStartDate( selectDate[ 0 ]+ " 00:00:00");
            payUsdtRecharge.setSelectEndDate( selectDate[ 1 ] + " 23:59:59" );
        }
        return payUsdtRechargeMapper.selectPayUsdtRechargeList(payUsdtRecharge);
    }

    /**
     * 新增USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return 结果
     */
    @Override
    public int insertPayUsdtRecharge(PayUsdtRecharge payUsdtRecharge) {
        payUsdtRecharge.setCreateTime(DateUtils.getNowDate());
        return payUsdtRechargeMapper.insertPayUsdtRecharge(payUsdtRecharge);
    }

    /**
     * 修改USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return 结果
     */
    @Override
    public int updatePayUsdtRecharge(PayUsdtRecharge payUsdtRecharge) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        payUsdtRecharge.setOpName(userName);
        payUsdtRecharge.setUpdateTime(new Date());
        return payUsdtRechargeMapper.updatePayUsdtRecharge(payUsdtRecharge);
    }

    /**
     * 批量删除USDT充值提交记录
     *
     * @param ids 需要删除的USDT充值提交记录ID
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeByIds(Long[] ids) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeByIds(ids);
    }

    /**
     * 删除USDT充值提交记录信息
     *
     * @param id USDT充值提交记录ID
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeById(Long id) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeById(id);
    }
}
