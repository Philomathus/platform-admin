package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.domain.WheelLottery;
import com.qiqilm.server.admin.mapper.WheelLotteryMapper;
import com.qiqilm.server.admin.service.IWheelLotteryService;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转盘彩票Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-01
 */
@Service
public class WheelLotteryServiceImpl implements IWheelLotteryService {
    @Autowired
    private WheelLotteryMapper wheelLotteryMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LiveCacheUtil liveCacheUtil;
    /**
     * 查询转盘彩票
     *
     * @param id 转盘彩票ID
     * @return 转盘彩票
     */
    @Override
    public WheelLottery selectWheelLotteryById(String id) {
        return wheelLotteryMapper.selectWheelLotteryById(id);
    }

    /**
     * 查询转盘彩票列表
     *
     * @param wheelLottery 转盘彩票
     * @return 转盘彩票
     */
    @Override
    public List<WheelLottery> selectWheelLotteryList(WheelLottery wheelLottery) {
        return wheelLotteryMapper.selectWheelLotteryList(wheelLottery);
    }

    /**
     * 新增转盘彩票
     *
     * @param wheelLottery 转盘彩票
     * @return 结果
     */
    @Override
    public int insertWheelLottery(WheelLottery wheelLottery) {
        int i = 0;
        try {
            i = wheelLotteryMapper.insertWheelLottery(wheelLottery);
        } catch (Exception e) {
            return i;
        }
        liveCacheUtil.setWheelLottery(wheelLottery);
        return i;
    }

    /**
     * 修改转盘彩票
     *
     * @param wheelLottery 转盘彩票
     * @return 结果
     */
    @Override
    public int updateWheelLottery(WheelLottery wheelLottery) {
        int i = wheelLotteryMapper.updateWheelLottery(wheelLottery);
        liveCacheUtil.setWheelLottery(wheelLottery);
        return i;
    }

    /**
     * 批量删除转盘彩票
     *
     * @param ids 需要删除的转盘彩票ID
     * @return 结果
     */
    @Override
    public int deleteWheelLotteryByIds(String[] ids) {
        return wheelLotteryMapper.deleteWheelLotteryByIds(ids);
    }

    /**
     * 删除转盘彩票信息
     *
     * @param id 转盘彩票ID
     * @return 结果
     */
    @Override
    public int deleteWheelLotteryById(String id) {
        int i = wheelLotteryMapper.deleteWheelLotteryById(id);
        liveCacheUtil.delWheelLotteryUseKey(Integer.parseInt(id));
        return i;
    }
}
