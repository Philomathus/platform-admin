package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.WheelUserMapper;
import com.qiqilm.server.admin.domain.WheelUser;
import com.qiqilm.server.admin.service.IWheelUserService;

/**
 * 转盘用户Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-08
 */
@Service
public class WheelUserServiceImpl implements IWheelUserService {
    @Autowired
    private WheelUserMapper wheelUserMapper;

    /**
     * 查询转盘用户
     *
     * @param id 转盘用户ID
     * @return 转盘用户
     */
    @Override
    public WheelUser selectWheelUserById(String id) {
        return wheelUserMapper.selectWheelUserById(id);
    }

    /**
     * 查询转盘用户列表
     *
     * @param wheelUser 转盘用户
     * @return 转盘用户
     */
    @Override
    public List<WheelUser> selectWheelUserList(WheelUser wheelUser) {
        return wheelUserMapper.selectWheelUserList(wheelUser);
    }

    /**
     * 新增转盘用户
     *
     * @param wheelUser 转盘用户
     * @return 结果
     */
    @Override
    public int insertWheelUser(WheelUser wheelUser) {
        return wheelUserMapper.insertWheelUser(wheelUser);
    }

    /**
     * 修改转盘用户
     *
     * @param wheelUser 转盘用户
     * @return 结果
     */
    @Override
    public int updateWheelUser(WheelUser wheelUser) {
        return wheelUserMapper.updateWheelUser(wheelUser);
    }

    /**
     * 批量删除转盘用户
     *
     * @param ids 需要删除的转盘用户ID
     * @return 结果
     */
    @Override
    public int deleteWheelUserByIds(String[] ids) {
        return wheelUserMapper.deleteWheelUserByIds(ids);
    }

    /**
     * 删除转盘用户信息
     *
     * @param id 转盘用户ID
     * @return 结果
     */
    @Override
    public int deleteWheelUserById(String id) {
        return wheelUserMapper.deleteWheelUserById(id);
    }
}
