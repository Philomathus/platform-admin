package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.SystemIpWhite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.SystemIpTypeWhiteMapper;
import com.qiqilm.server.admin.domain.SystemIpTypeWhite;
import com.qiqilm.server.admin.service.ISystemIpTypeWhiteService;

import javax.annotation.Resource;

/**
 * IP黑名单或反作弊禁言Service业务层处理
 *
 * @author 77tv
 * @date 2021-07-12
 */
@Service
public class SystemIpTypeWhiteServiceImpl implements ISystemIpTypeWhiteService {
    @Resource
    private SystemIpTypeWhiteMapper systemIpTypeWhiteMapper;

    /**
     * 查询IP黑名单或反作弊禁言
     *
     * @param id IP黑名单或反作弊禁言ID
     * @return IP黑名单或反作弊禁言
     */
    @Override
    public SystemIpTypeWhite selectSystemIpTypeWhiteById(String id) {
        return systemIpTypeWhiteMapper.selectSystemIpTypeWhiteById(id);
    }

    /**
     * 查询IP黑名单或反作弊禁言列表
     *
     * @param systemIpTypeWhite IP黑名单或反作弊禁言
     * @return IP黑名单或反作弊禁言
     */
    @Override
    public List<SystemIpTypeWhite> selectSystemIpTypeWhiteList(SystemIpTypeWhite systemIpTypeWhite) {
        return systemIpTypeWhiteMapper.selectSystemIpTypeWhiteList(systemIpTypeWhite);
    }

    /**
     * 新增IP黑名单或反作弊禁言
     *
     * @param systemIpTypeWhite IP黑名单或反作弊禁言
     * @return 结果
     */
    @Override
    public int insertSystemIpTypeWhite(SystemIpTypeWhite systemIpTypeWhite) {
        return systemIpTypeWhiteMapper.insertSystemIpTypeWhite(systemIpTypeWhite);
    }

    /**
     * 修改IP黑名单或反作弊禁言
     *
     * @param systemIpTypeWhite IP黑名单或反作弊禁言
     * @return 结果
     */
    @Override
    public int updateSystemIpTypeWhite(SystemIpTypeWhite systemIpTypeWhite) {
        return systemIpTypeWhiteMapper.updateSystemIpTypeWhite(systemIpTypeWhite);
    }

    /**
     * 批量删除IP黑名单或反作弊禁言
     *
     * @param ids 需要删除的IP黑名单或反作弊禁言ID
     * @return 结果
     */
    @Override
    public int deleteSystemIpTypeWhiteByIds(String[] ids) {
        return systemIpTypeWhiteMapper.deleteSystemIpTypeWhiteByIds(ids);
    }


    /**
     * 删除IP黑名单或反作弊禁言信息
     *
     * @param id IP黑名单或反作弊禁言ID
     * @return 结果
     */
    @Override
    public int deleteSystemIpTypeWhiteById(String id) {
        return systemIpTypeWhiteMapper.deleteSystemIpTypeWhiteById(id);
    }

    @Override
    public int exists(String value) {
        return systemIpTypeWhiteMapper.exists(value);
    }
}
