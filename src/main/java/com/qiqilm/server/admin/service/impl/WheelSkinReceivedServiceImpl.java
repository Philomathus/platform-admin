package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.WheelSkinReceived;
import com.qiqilm.server.admin.domain.dto.WheelSkinReceivedExcel;
import com.qiqilm.server.admin.mapper.WheelSkinReceivedMapper;
import com.qiqilm.server.admin.service.IWheelSkinReceivedService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 转盘皮肤领取Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-24
 */
@Service
public class WheelSkinReceivedServiceImpl implements IWheelSkinReceivedService {
    @Autowired
    private WheelSkinReceivedMapper wheelSkinReceivedMapper;

    /**
     * 查询转盘皮肤领取
     *
     * @param id 转盘皮肤领取ID
     * @return 转盘皮肤领取
     */
    @Override
    public WheelSkinReceived selectWheelSkinReceivedById(Long id) {
        return wheelSkinReceivedMapper.selectWheelSkinReceivedById(id);
    }

    /**
     * 查询转盘皮肤领取列表
     *
     * @param wheelSkinReceived 转盘皮肤领取
     * @return 转盘皮肤领取
     */
    @Override
    public List<WheelSkinReceived> selectWheelSkinReceivedList(WheelSkinReceived wheelSkinReceived) {
        return wheelSkinReceivedMapper.selectWheelSkinReceivedList(wheelSkinReceived);
    }

    /**
     * 新增转盘皮肤领取
     *
     * @param wheelSkinReceived 转盘皮肤领取
     * @return 结果
     */
    @Override
    public int insertWheelSkinReceived(WheelSkinReceived wheelSkinReceived) {
        wheelSkinReceived.setCreateTime(DateUtils.getNowDate());
        return wheelSkinReceivedMapper.insertWheelSkinReceived(wheelSkinReceived);
    }

    /**
     * 修改转盘皮肤领取
     *
     * @param wheelSkinReceived 转盘皮肤领取
     * @return 结果
     */
    @Override
    public int updateWheelSkinReceived(WheelSkinReceived wheelSkinReceived) {
//        wheelSkinReceived.setUpdateTime(DateUtils.getNowDate());
        return wheelSkinReceivedMapper.updateWheelSkinReceived(wheelSkinReceived);
    }

    /**
     * 批量删除转盘皮肤领取
     *
     * @param ids 需要删除的转盘皮肤领取ID
     * @return 结果
     */
    @Override
    public int deleteWheelSkinReceivedByIds(Long[] ids) {
        return wheelSkinReceivedMapper.deleteWheelSkinReceivedByIds(ids);
    }

    /**
     * 删除转盘皮肤领取信息
     *
     * @param id 转盘皮肤领取ID
     * @return 结果
     */
    @Override
    public int deleteWheelSkinReceivedById(Long id) {
        return wheelSkinReceivedMapper.deleteWheelSkinReceivedById(id);
    }

    @Override
    public Map getTotal(WheelSkinReceived wheelSkinReceived) {
        return wheelSkinReceivedMapper.getTotal(wheelSkinReceived);
    }

    @Override
    public List<WheelSkinReceivedExcel> selectWheelSkinReceivedList2(WheelSkinReceived wheelSkinReceived) {
        return wheelSkinReceivedMapper.selectWheelSkinReceivedList2(wheelSkinReceived);
    }
}
