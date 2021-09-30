package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.PayAgentCardMapper;
import com.qiqilm.server.admin.domain.PayAgentCard;
import com.qiqilm.server.admin.service.IPayAgentCardService;
import org.springframework.util.CollectionUtils;

/**
 * 代充人银行卡列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-24
 */
@Service
public class PayAgentCardServiceImpl implements IPayAgentCardService {
    @Autowired
    private PayAgentCardMapper payAgentCardMapper;
    @Autowired
    private TokenService tokenService;
    /**
     * 查询代充人银行卡
     *
     * @param id 代充人银行卡ID
     * @return 代充人银行卡
     */
    @Override
    public PayAgentCard selectPayAgentCardById(Long id) {
        return payAgentCardMapper.selectPayAgentCardById(id);
    }

    /**
     * 查询代充人银行卡列表
     *
     * @param payAgentCard 代充人银行卡列表
     * @return 代充人银行卡列表
     */
    @Override
    public List<PayAgentCard> selectPayAgentCardList(PayAgentCard payAgentCard) {
        return payAgentCardMapper.selectPayAgentCardList(payAgentCard);
    }

    /**
     * 新增代充人银行卡列表
     *
     * @param payAgentCard 代充人银行卡列表
     * @return 结果
     */
    @Override
    public int insertPayAgentCard(PayAgentCard payAgentCard) {
        PayAgentCard payAgentCards = new PayAgentCard();
        payAgentCards.setAgentId(payAgentCard.getAgentId());
        payAgentCards.setBankAccount(payAgentCard.getBankAccount());
        List<PayAgentCard> list = payAgentCardMapper.selectPayAgentCardList(payAgentCards);
        if(!CollectionUtils.isEmpty(list)){
            return 0;
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userName = loginUser.getUser().getUserName();
        payAgentCard.setCreateBy(userName);
        payAgentCard.setCreateTime(DateUtils.getNowDate());
        payAgentCard.setStatus("0");
        return payAgentCardMapper.insertPayAgentCard(payAgentCard);
    }

    /**
     * 修改代充人银行卡列表
     *
     * @param payAgentCard 代充人银行卡列表
     * @return 结果
     */
    @Override
    public int updatePayAgentCard(PayAgentCard payAgentCard) {
        PayAgentCard payAgentCards = new PayAgentCard();
        payAgentCards.setId(payAgentCard.getId());
        payAgentCards.setAgentId(payAgentCard.getAgentId());
        payAgentCards.setBankAccount(payAgentCard.getBankAccount());
        List<PayAgentCard> list = payAgentCardMapper.selectOtherPayAgentCardsById(payAgentCard);
        if(!CollectionUtils.isEmpty(list)){
            return 0;
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userName = loginUser.getUser().getUserName();
        payAgentCard.setOperator(userName);
        payAgentCard.setOperatorTime(DateUtils.getTime());
        return payAgentCardMapper.updatePayAgentCard(payAgentCard);
    }

    /**
     * 批量删除代充人银行卡列表
     *
     * @param ids 需要删除的代充人银行卡列表ID
     * @return 结果
     */
    @Override
    public int deletePayAgentCardByIds(Long[] ids) {
        return payAgentCardMapper.deletePayAgentCardByIds(ids);
    }

    /**
     * 删除代充人银行卡列表信息
     *
     * @param id 代充人银行卡列表ID
     * @return 结果
     */
    @Override
    public int deletePayAgentCardById(Long id) {
        return payAgentCardMapper.deletePayAgentCardById(id);
    }
}
