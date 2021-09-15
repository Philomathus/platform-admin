package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.WheelHistoryDice;
import com.qiqilm.server.admin.mapper.WheelHistoryDiceMapper;
import com.qiqilm.server.admin.service.IWheelHistoryDiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博饼中奖记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-02
 */
@Service
public class WheelHistoryDiceServiceImpl implements IWheelHistoryDiceService {
    @Resource
    private WheelHistoryDiceMapper wheelHistoryDiceMapper;

    /**
     * 查询博饼中奖记录
     *
     * @param id 博饼中奖记录ID
     * @return 博饼中奖记录
     */
    @Override
    public WheelHistoryDice selectWheelHistoryDiceById(Long id) {
        return wheelHistoryDiceMapper.selectWheelHistoryDiceById(id);
    }

    /**
     * 查询博饼中奖记录列表
     *
     * @param wheelHistoryDice 博饼中奖记录
     * @return 博饼中奖记录
     */
    @Override
    public List<WheelHistoryDice> selectWheelHistoryDiceList(WheelHistoryDice wheelHistoryDice) {
        return wheelHistoryDiceMapper.selectWheelHistoryDiceList(wheelHistoryDice);
    }

    /**
     * 新增博饼中奖记录
     *
     * @param wheelHistoryDice 博饼中奖记录
     * @return 结果
     */
    @Override
    public int insertWheelHistoryDice(WheelHistoryDice wheelHistoryDice) {
        return wheelHistoryDiceMapper.insertWheelHistoryDice(wheelHistoryDice);
    }

    /**
     * 修改博饼中奖记录
     *
     * @param wheelHistoryDice 博饼中奖记录
     * @return 结果
     */
    @Override
    public int updateWheelHistoryDice(WheelHistoryDice wheelHistoryDice) {
        return wheelHistoryDiceMapper.updateWheelHistoryDice(wheelHistoryDice);
    }

    /**
     * 批量删除博饼中奖记录
     *
     * @param ids 需要删除的博饼中奖记录ID
     * @return 结果
     */
    @Override
    public int deleteWheelHistoryDiceByIds(Long[] ids) {
        return wheelHistoryDiceMapper.deleteWheelHistoryDiceByIds(ids);
    }

    /**
     * 删除博饼中奖记录信息
     *
     * @param id 博饼中奖记录ID
     * @return 结果
     */
    @Override
    public int deleteWheelHistoryDiceById(Long id) {
        return wheelHistoryDiceMapper.deleteWheelHistoryDiceById(id);
    }
}
