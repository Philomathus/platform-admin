package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.WheelDiceMapper;
import com.qiqilm.server.admin.domain.WheelDice;
import com.qiqilm.server.admin.service.IWheelDiceService;

/**
 * 中秋博饼Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-02
 */
@Service
public class WheelDiceServiceImpl implements IWheelDiceService {
    @Autowired
    private WheelDiceMapper wheelDiceMapper;

    /**
     * 查询中秋博饼
     *
     * @param id 中秋博饼ID
     * @return 中秋博饼
     */
    @Override
    public WheelDice selectWheelDiceById(Long id) {
        return wheelDiceMapper.selectWheelDiceById(id);
    }

    /**
     * 查询中秋博饼列表
     *
     * @param wheelDice 中秋博饼
     * @return 中秋博饼
     */
    @Override
    public List<WheelDice> selectWheelDiceList(WheelDice wheelDice) {
        return wheelDiceMapper.selectWheelDiceList(wheelDice);
    }

    /**
     * 新增中秋博饼
     *
     * @param wheelDice 中秋博饼
     * @return 结果
     */
    @Override
    public int insertWheelDice(WheelDice wheelDice) {
        return wheelDiceMapper.insertWheelDice(wheelDice);
    }

    /**
     * 修改中秋博饼
     *
     * @param wheelDice 中秋博饼
     * @return 结果
     */
    @Override
    public int updateWheelDice(WheelDice wheelDice) {
        return wheelDiceMapper.updateWheelDice(wheelDice);
    }

    /**
     * 批量删除中秋博饼
     *
     * @param ids 需要删除的中秋博饼ID
     * @return 结果
     */
    @Override
    public int deleteWheelDiceByIds(Long[] ids) {
        return wheelDiceMapper.deleteWheelDiceByIds(ids);
    }

    /**
     * 删除中秋博饼信息
     *
     * @param id 中秋博饼ID
     * @return 结果
     */
    @Override
    public int deleteWheelDiceById(Long id) {
        return wheelDiceMapper.deleteWheelDiceById(id);
    }
}
