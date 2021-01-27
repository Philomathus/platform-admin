package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.SystemIpWhiteMapper;
import com.qiqilm.server.admin.domain.SystemIpWhite;
import com.qiqilm.server.admin.service.ISystemIpWhiteService;

/**
 * IP白名单Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class SystemIpWhiteServiceImpl implements ISystemIpWhiteService {
    @Autowired
    private SystemIpWhiteMapper systemIpWhiteMapper;

    /**
     * 查询IP白名单
     *
     * @param ipId IP白名单ID
     * @return IP白名单
     */
    @Override
    public SystemIpWhite selectSystemIpWhiteById(String ipId) {
        return systemIpWhiteMapper.selectSystemIpWhiteById(ipId);
    }

    /**
     * 查询IP白名单列表
     *
     * @param systemIpWhite IP白名单
     * @return IP白名单
     */
    @Override
    public List<SystemIpWhite> selectSystemIpWhiteList(SystemIpWhite systemIpWhite) {
        return systemIpWhiteMapper.selectSystemIpWhiteList(systemIpWhite);
    }

    /**
     * 新增IP白名单
     *
     * @param systemIpWhite IP白名单
     * @return 结果
     */
    @Override
    public int insertSystemIpWhite(SystemIpWhite systemIpWhite) {
        return systemIpWhiteMapper.insertSystemIpWhite(systemIpWhite);
    }

    /**
     * 修改IP白名单
     *
     * @param systemIpWhite IP白名单
     * @return 结果
     */
    @Override
    public int updateSystemIpWhite(SystemIpWhite systemIpWhite) {
        return systemIpWhiteMapper.updateSystemIpWhite(systemIpWhite);
    }

    /**
     * 批量删除IP白名单
     *
     * @param ipIds 需要删除的IP白名单ID
     * @return 结果
     */
    @Override
    public int deleteSystemIpWhiteByIds(String[] ipIds) {
        return systemIpWhiteMapper.deleteSystemIpWhiteByIds(ipIds);
    }

    /**
     * 删除IP白名单信息
     *
     * @param ipId IP白名单ID
     * @return 结果
     */
    @Override
    public int deleteSystemIpWhiteById(String ipId) {
        return systemIpWhiteMapper.deleteSystemIpWhiteById(ipId);
    }
}
