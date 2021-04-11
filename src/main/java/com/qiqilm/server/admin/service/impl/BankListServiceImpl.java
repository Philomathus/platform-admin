package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.BankListMapper;
import com.qiqilm.server.admin.domain.BankList;
import com.qiqilm.server.admin.service.IBankListService;
import org.springframework.util.CollectionUtils;

/**
 * 出款银行列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-06
 */
@Service
public class BankListServiceImpl implements IBankListService {
    @Autowired
    private BankListMapper bankListMapper;

    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;

    /**
     * 查询出款银行列表
     *
     * @param id 出款银行列表ID
     * @return 出款银行列表
     */
    @Override
    public BankList selectBankListById(Long id) {
        return bankListMapper.selectBankListById(id);
    }

    /**
     * 查询出款银行列表列表
     *
     * @param bankList 出款银行列表
     * @return 出款银行列表
     */
    @Override
    public List<BankList> selectBankListList(BankList bankList) {
        List<BankList> bankLists = bankListMapper.selectBankListList(bankList);
        if ( !CollectionUtils.isEmpty( bankLists ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( BankList ba : bankLists ) {
                if ( StringUtils.isNotBlank( ba.getBankIcon() ) && !ba.getBankIcon().startsWith( "http" ) ) {
                    ba.setBankIcon( domainValue + ba.getBankIcon() );
                }
            }
        }
        return bankLists;
    }

    /**
     * 新增出款银行列表
     *
     * @param bankList 出款银行列表
     * @return 结果
     */
    @Override
    public int insertBankList(BankList bankList) {
        return bankListMapper.insertBankList(bankList);
    }

    /**
     * 修改出款银行列表
     *
     * @param bankList 出款银行列表
     * @return 结果
     */
    @Override
    public int updateBankList(BankList bankList) {
        return bankListMapper.updateBankList(bankList);
    }

    /**
     * 批量删除出款银行列表
     *
     * @param ids 需要删除的出款银行列表ID
     * @return 结果
     */
    @Override
    public int deleteBankListByIds(Long[] ids) {
        return bankListMapper.deleteBankListByIds(ids);
    }

    /**
     * 删除出款银行列表信息
     *
     * @param id 出款银行列表ID
     * @return 结果
     */
    @Override
    public int deleteBankListById(Long id) {
        return bankListMapper.deleteBankListById(id);
    }
}
