package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.mapper.ConfigBankMapper;
import com.qiqilm.server.admin.service.IConfigBankService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ConfigBankServiceImpl implements IConfigBankService {
    @Autowired
    private ConfigBankMapper configBankMapper;
    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigBank selectConfigBankById(String id) {
        return configBankMapper.selectConfigBankById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configBank 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigBank> selectConfigBankList(ConfigBank configBank) {
        List<ConfigBank> configBanks = configBankMapper.selectConfigBankList(configBank);
        if ( !CollectionUtils.isEmpty( configBanks ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( ConfigBank Bank : configBanks ) {
                if ( StringUtils.isNotBlank( Bank.getIcon() ) && !Bank.getIcon().startsWith( "http" ) ) {
                    Bank.setIcon( domainValue + Bank.getIcon() );
                }
            }
        }
        return configBanks;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigBank(ConfigBank configBank) {
        configBank.setCreateTime(DateUtils.getNowDate());
        return configBankMapper.insertConfigBank(configBank);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigBank(ConfigBank configBank) {
        configBank.setUpdateTime(DateUtils.getNowDate());
        return configBankMapper.updateConfigBank(configBank);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigBankByIds(String[] ids) {
        return configBankMapper.deleteConfigBankByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigBankById(String id) {
        return configBankMapper.deleteConfigBankById(id);
    }
}
