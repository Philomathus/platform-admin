package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.WheelUserDiceMapper;
import com.qiqilm.server.admin.domain.WheelUserDice;
import com.qiqilm.server.admin.service.IWheelUserDiceService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-02
 */
@Service
public class WheelUserDiceServiceImpl implements IWheelUserDiceService {
    @Autowired
    private WheelUserDiceMapper wheelUserDiceMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public WheelUserDice selectWheelUserDiceById(String id) {
        return wheelUserDiceMapper.selectWheelUserDiceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param wheelUserDice 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<WheelUserDice> selectWheelUserDiceList(WheelUserDice wheelUserDice) {
        return wheelUserDiceMapper.selectWheelUserDiceList(wheelUserDice);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param wheelUserDice 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertWheelUserDice(WheelUserDice wheelUserDice) {
        return wheelUserDiceMapper.insertWheelUserDice(wheelUserDice);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param wheelUserDice 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateWheelUserDice(WheelUserDice wheelUserDice) {
        return wheelUserDiceMapper.updateWheelUserDice(wheelUserDice);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteWheelUserDiceByIds(String[] ids) {
        return wheelUserDiceMapper.deleteWheelUserDiceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteWheelUserDiceById(String id) {
        return wheelUserDiceMapper.deleteWheelUserDiceById(id);
    }
}
