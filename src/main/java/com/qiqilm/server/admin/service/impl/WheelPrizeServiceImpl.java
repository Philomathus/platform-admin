package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.WheelPrizeMapper;
import com.qiqilm.server.admin.domain.WheelPrize;
import com.qiqilm.server.admin.service.IWheelPrizeService;

/**
 * 转盘奖励Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Service
public class WheelPrizeServiceImpl implements IWheelPrizeService {
    @Autowired
    private WheelPrizeMapper wheelPrizeMapper;

    /**
     * 查询转盘奖励
     *
     * @param id 转盘奖励ID
     * @return 转盘奖励
     */
    @Override
    public WheelPrize selectWheelPrizeById(Long id) {
        return wheelPrizeMapper.selectWheelPrizeById(id);
    }

    /**
     * 查询转盘奖励列表
     *
     * @param wheelPrize 转盘奖励
     * @return 转盘奖励
     */
    @Override
    public List<WheelPrize> selectWheelPrizeList(WheelPrize wheelPrize) {
        return wheelPrizeMapper.selectWheelPrizeList(wheelPrize);
    }

    /**
     * 新增转盘奖励
     *
     * @param wheelPrize 转盘奖励
     * @return 结果
     */
    @Override
    public int insertWheelPrize(WheelPrize wheelPrize) {
        return wheelPrizeMapper.insertWheelPrize(wheelPrize);
    }

    /**
     * 修改转盘奖励
     *
     * @param wheelPrize 转盘奖励
     * @return 结果
     */
    @Override
    public int updateWheelPrize(WheelPrize wheelPrize) {
        return wheelPrizeMapper.updateWheelPrize(wheelPrize);
    }

    /**
     * 批量删除转盘奖励
     *
     * @param ids 需要删除的转盘奖励ID
     * @return 结果
     */
    @Override
    public int deleteWheelPrizeByIds(Long[] ids) {
        return wheelPrizeMapper.deleteWheelPrizeByIds(ids);
    }

    /**
     * 删除转盘奖励信息
     *
     * @param id 转盘奖励ID
     * @return 结果
     */
    @Override
    public int deleteWheelPrizeById(Long id) {
        return wheelPrizeMapper.deleteWheelPrizeById(id);
    }
}
