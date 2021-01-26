package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.service.ILiveUserService;

/**
 * //用户信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveUserServiceImpl implements ILiveUserService {
    @Autowired
    private LiveUserMapper liveUserMapper;

    /**
     * 查询//用户信息
     *
     * @param id //用户信息ID
     * @return //用户信息
     */
    @Override
    public LiveUser selectLiveUserById(Long id) {
        return liveUserMapper.selectLiveUserById(id);
    }

    /**
     * 查询//用户信息列表
     *
     * @param liveUser //用户信息
     * @return //用户信息
     */
    @Override
    public List<LiveUser> selectLiveUserList(LiveUser liveUser) {
        return liveUserMapper.selectLiveUserList(liveUser);
    }

    /**
     * 新增//用户信息
     *
     * @param liveUser //用户信息
     * @return 结果
     */
    @Override
    public int insertLiveUser(LiveUser liveUser) {
        liveUser.setCreateTime(DateUtils.getNowDate());
        return liveUserMapper.insertLiveUser(liveUser);
    }

    /**
     * 修改//用户信息
     *
     * @param liveUser //用户信息
     * @return 结果
     */
    @Override
    public int updateLiveUser(LiveUser liveUser) {
        liveUser.setUpdateTime(DateUtils.getNowDate());
        return liveUserMapper.updateLiveUser(liveUser);
    }

    /**
     * 批量删除//用户信息
     *
     * @param ids 需要删除的//用户信息ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserByIds(Long[] ids) {
        return liveUserMapper.deleteLiveUserByIds(ids);
    }

    /**
     * 删除//用户信息信息
     *
     * @param id //用户信息ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserById(Long id) {
        return liveUserMapper.deleteLiveUserById(id);
    }
}
