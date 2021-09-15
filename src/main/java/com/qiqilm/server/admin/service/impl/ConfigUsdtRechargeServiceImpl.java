package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.ConfigUsdtRecharge;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigUsdtRechargeMapper;

import com.qiqilm.server.admin.service.IConfigUsdtRechargeService;
import org.springframework.util.CollectionUtils;

/**
 * USDT渠道信息 Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-11
 */
@Service
public class ConfigUsdtRechargeServiceImpl implements IConfigUsdtRechargeService {
    @Autowired
    private ConfigUsdtRechargeMapper configUsdtRechargeMapper;
    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;
    @Autowired
    private TokenService tokenService;
    /**
     * 查询 USDT渠道信息
     *
     * @param id USDT渠道信息ID
     * @return USDT渠道信息
     */
    @Override
    public ConfigUsdtRecharge selectConfigUsdtRechargeById(String id) {
        return configUsdtRechargeMapper.selectConfigUsdtRechargeById(id);
    }

    /**
     * 查询 USDT渠道信息列表
     *
     * @param configUsdtRecharge USDT渠道信息
     * @return USDT渠道信息列表
     */
    @Override
    public List<ConfigUsdtRecharge> selectConfigUsdtRechargeList(ConfigUsdtRecharge configUsdtRecharge) {
        List<ConfigUsdtRecharge> configUsdtRecharges = configUsdtRechargeMapper.selectConfigUsdtRechargeList(configUsdtRecharge);
        if ( !CollectionUtils.isEmpty( configUsdtRecharges ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( ConfigUsdtRecharge info : configUsdtRecharges ) {
                if ( StringUtils.isNotBlank( info.getIcon() ) && !info.getIcon().startsWith( "http" ) ) {
                    info.setIcon( domainValue + info.getIcon() );
                }
            }
        }
        return configUsdtRecharges;
    }

    /**
     * 新增 USDT渠道信息
     *
     * @param configUsdtRecharge USDT渠道信息
     * @return 结果
     */
    @Override
    public int insertConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userName = loginUser.getUser().getUserName();
        configUsdtRecharge.setCreateBy(userName);
        configUsdtRecharge.setCreateTime(DateUtils.getNowDate());
        return configUsdtRechargeMapper.insertConfigUsdtRecharge(configUsdtRecharge);
    }

    /**
     * 修改 USDT渠道信息
     *
     * @param configUsdtRecharge USDT渠道信息
     * @return 结果
     */
    @Override
    public int updateConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userName = loginUser.getUser().getUserName();
        configUsdtRecharge.setCreateBy(userName);
        configUsdtRecharge.setUpdateTime(DateUtils.getNowDate());
        return configUsdtRechargeMapper.updateConfigUsdtRecharge(configUsdtRecharge);
    }

    /**
     * 批量删除 USDT渠道信息
     *
     * @param ids 需要删除的USDT渠道信息ID
     * @return 结果
     */
    @Override
    public int deleteConfigUsdtRechargeByIds(String[] ids) {
        return configUsdtRechargeMapper.deleteConfigUsdtRechargeByIds(ids);
    }

    /**
     * 删除 USDT渠道信息
     *
     * @param id USDT渠道信息ID
     * @return 结果
     */
    @Override
    public int deleteConfigUsdtRechargeById(String id) {
        return configUsdtRechargeMapper.deleteConfigUsdtRechargeById(id);
    }
}
