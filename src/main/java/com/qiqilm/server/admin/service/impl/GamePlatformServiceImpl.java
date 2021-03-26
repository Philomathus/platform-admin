package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class GamePlatformServiceImpl implements IGamePlatformService {
    @Autowired
    private GamePlatformMapper gamePlatformMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private GameCacheManager gameCacheManager;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public GamePlatform selectGamePlatformById(Integer id) {
        return gamePlatformMapper.selectGamePlatformById(id);
    }


    @Override
    public int changeStatus(GamePlatform gamePlatform) {
        int i = gamePlatformMapper.changeStattus(gamePlatform);
        gameCacheManager.initGamePlatforms();
        return i;
    }
    /**
     * 查询【请填写功能名称】列表
     *
     * @param gamePlatform 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<GamePlatform> selectGamePlatformList(GamePlatform gamePlatform) {
        return gamePlatformMapper.selectGamePlatformList(gamePlatform);
    }



    /**
     * 新增【请填写功能名称】
     *
     * @param gamePlatform 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertGamePlatform(GamePlatform gamePlatform) {
        gamePlatform.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String        username  = loginUser.getUsername();
        gamePlatform.setCreateBy(username);
        int i = gamePlatformMapper.insertGamePlatform(gamePlatform);
        gameCacheManager.initGamePlatforms();
        return i;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param gamePlatform 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateGamePlatform(GamePlatform gamePlatform) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String        username  = loginUser.getUsername();
        gamePlatform.setUpdateBy(username);
        gamePlatform.setUpdateTime(new Date());
        int i = gamePlatformMapper.updateGamePlatform(gamePlatform);
        gameCacheManager.initGamePlatforms();
        return i;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteGamePlatformByIds(Long[] ids) {
        int i = gamePlatformMapper.deleteGamePlatformByIds(ids);
        gameCacheManager.initGamePlatforms();
        return i ;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteGamePlatformById(Long id) {
        int i = gamePlatformMapper.deleteGamePlatformById(id);
        gameCacheManager.initGamePlatforms();
        return i;
    }
}