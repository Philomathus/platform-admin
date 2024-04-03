package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.ConfigVip;
import com.qiqilm.server.admin.mapper.ConfigVipMapper;
import com.qiqilm.server.admin.service.IConfigVipService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-02
 */
@Service
public class ConfigVipServiceImpl implements IConfigVipService {
    @Autowired
    private ConfigVipMapper configVipMapper;
    @Autowired
    private TokenService    tokenService;

    @Autowired
    private GameCacheManager gameCacheManager;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     *
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigVip selectConfigVipById( String id ) {
        return configVipMapper.selectConfigVipById( id );
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configVip 【请填写功能名称】
     *
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigVip> selectConfigVipList( ConfigVip configVip ) {
        return configVipMapper.selectConfigVipList( configVip );
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configVip 【请填写功能名称】
     *
     * @return 结果
     */
    @Override
    public AjaxResult insertConfigVip( ConfigVip configVip ) {
        int       levelFlag  = configVip.getLevelFlag();
        ConfigVip configVip1 = configVipMapper.selectConfigVip( levelFlag );
        if ( Objects.nonNull( configVip1 ) ) {
            return AjaxResult.success( "vip" + configVip.getLevelFlag() + "已被创建,请勿重复创建" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        configVip.setId( UuidUtil.getRandomUuid() );
        configVip.setOpName( userName );
        configVip.setCreateTime( DateUtils.getNowDate() );
        configVip.setUpdateTime( DateUtils.getNowDate() );
        configVipMapper.insertConfigVip( configVip );
        gameCacheManager.initVip();
        return AjaxResult.success( "新增成功" );
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configVip 【请填写功能名称】
     *
     * @return 结果
     */
    @Override
    public AjaxResult updateConfigVip( ConfigVip configVip ) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        configVip.setUpdateTime( DateUtils.getNowDate() );
        configVip.setOpName( userName );
        configVipMapper.updateConfigVip( configVip );
        gameCacheManager.initVip();
        return AjaxResult.success( "编辑成功" );
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     *
     * @return 结果
     */
    @Override
    public int deleteConfigVipByIds( String[] ids ) {
        int i = configVipMapper.deleteConfigVipByIds( ids );
        gameCacheManager.initVip();
        return i;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     *
     * @return 结果
     */
    @Override
    public int deleteConfigVipById( String id ) {
        int i = configVipMapper.deleteConfigVipById( id );
        gameCacheManager.initVip();
        return i;
    }
}