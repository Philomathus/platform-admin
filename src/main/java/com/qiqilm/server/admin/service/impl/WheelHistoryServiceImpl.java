package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.WheelHistory;
import com.qiqilm.server.admin.mapper.WheelHistoryMapper;
import com.qiqilm.server.admin.service.IWheelHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转盘中奖历史Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-05
 */
@Service
public class WheelHistoryServiceImpl implements IWheelHistoryService {
    @Autowired
    private WheelHistoryMapper wheelHistoryMapper;

    /**
     * 查询转盘中奖历史
     *
     * @param id 转盘中奖历史ID
     * @return 转盘中奖历史
     */
    @Override
    public WheelHistory selectWheelHistoryById(Long id) {
        return wheelHistoryMapper.selectWheelHistoryById(id);
    }

    /**
     * 查询转盘中奖历史列表
     *
     * @param wheelHistory 转盘中奖历史
     * @return 转盘中奖历史
     */
    @Override
    public List<WheelHistory> selectWheelHistoryList(WheelHistory wheelHistory) {
        String[] selectDate = wheelHistory.getSelectDate();
        if (selectDate!=null && selectDate.length ==2) {
            wheelHistory.setSTime( selectDate[0]);
            wheelHistory.setETime( selectDate[1]);
        }

        return wheelHistoryMapper.selectWheelHistoryList(wheelHistory);
    }

    /**
     * 新增转盘中奖历史
     *
     * @param wheelHistory 转盘中奖历史
     * @return 结果
     */
    @Override
    public int insertWheelHistory(WheelHistory wheelHistory) {
        return wheelHistoryMapper.insertWheelHistory(wheelHistory);
    }

    /**
     * 修改转盘中奖历史
     *
     * @param wheelHistory 转盘中奖历史
     * @return 结果
     */
    @Override
    public int updateWheelHistory(WheelHistory wheelHistory) {
        return wheelHistoryMapper.updateWheelHistory(wheelHistory);
    }

    /**
     * 批量删除转盘中奖历史
     *
     * @param ids 需要删除的转盘中奖历史ID
     * @return 结果
     */
    @Override
    public int deleteWheelHistoryByIds(Long[] ids) {
        return wheelHistoryMapper.deleteWheelHistoryByIds(ids);
    }

    /**
     * 删除转盘中奖历史信息
     *
     * @param id 转盘中奖历史ID
     * @return 结果
     */
    @Override
    public int deleteWheelHistoryById(Long id) {
        return wheelHistoryMapper.deleteWheelHistoryById(id);
    }
}
