package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.WheelSkin;
import com.qiqilm.server.admin.mapper.WheelSkinMapper;
import com.qiqilm.server.admin.service.IWheelSkinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转盘皮肤列Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Service
public class WheelSkinServiceImpl implements IWheelSkinService {
    @Autowired
    private WheelSkinMapper wheelSkinMapper;

    /**
     * 查询转盘皮肤列
     *
     * @param id 转盘皮肤列ID
     * @return 转盘皮肤列
     */
    @Override
    public WheelSkin selectWheelSkinById(Long id) {
        return wheelSkinMapper.selectWheelSkinById(id);
    }

    /**
     * 查询转盘皮肤列列表
     *
     * @param wheelSkin 转盘皮肤列
     * @return 转盘皮肤列
     */
    @Override
    public List<WheelSkin> selectWheelSkinList(WheelSkin wheelSkin) {
        return wheelSkinMapper.selectWheelSkinList(wheelSkin);
    }

    /**
     * 新增转盘皮肤列
     *
     * @param wheelSkin 转盘皮肤列
     * @return 结果
     */
    @Override
    public int insertWheelSkin(WheelSkin wheelSkin) {
//        wheelSkin.setCreateTime(DateUtils.getNowDate());
        return wheelSkinMapper.insertWheelSkin(wheelSkin);
    }

    /**
     * 修改转盘皮肤列
     *
     * @param wheelSkin 转盘皮肤列
     * @return 结果
     */
    @Override
    public int updateWheelSkin(WheelSkin wheelSkin) {
//        wheelSkin.setUpdateTime(DateUtils.getNowDate());
        return wheelSkinMapper.updateWheelSkin(wheelSkin);
    }

    /**
     * 批量删除转盘皮肤列
     *
     * @param ids 需要删除的转盘皮肤列ID
     * @return 结果
     */
    @Override
    public int deleteWheelSkinByIds(Long[] ids) {
        return wheelSkinMapper.deleteWheelSkinByIds(ids);
    }

    /**
     * 删除转盘皮肤列信息
     *
     * @param id 转盘皮肤列ID
     * @return 结果
     */
    @Override
    public int deleteWheelSkinById(Long id) {
        return wheelSkinMapper.deleteWheelSkinById(id);
    }
}
