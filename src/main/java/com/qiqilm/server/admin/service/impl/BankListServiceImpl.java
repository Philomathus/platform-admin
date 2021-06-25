package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.BankList;
import com.qiqilm.server.admin.mapper.BankListMapper;
import com.qiqilm.server.admin.service.IBankListService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 出款银行列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-06
 */
@Service
public class BankListServiceImpl implements IBankListService {
    @Resource
    private BankListMapper bankListMapper;

    @Resource
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

    @Override
    public List<BankList> selectBankListLists() {
        BankList bankList=new BankList();
        return bankListMapper.selectBankListList(bankList);
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
