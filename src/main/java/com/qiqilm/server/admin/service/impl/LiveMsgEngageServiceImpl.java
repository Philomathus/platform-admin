package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveMsgEngageMapper;
import com.qiqilm.server.admin.domain.LiveMsgEngage;
import com.qiqilm.server.admin.service.ILiveMsgEngageService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-22
 */
@Service
public class LiveMsgEngageServiceImpl implements ILiveMsgEngageService {
    @Autowired
    private LiveMsgEngageMapper liveMsgEngageMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveMsgEngage selectLiveMsgEngageById(Long id) {
        return liveMsgEngageMapper.selectLiveMsgEngageById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveMsgEngage 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveMsgEngage> selectLiveMsgEngageList(LiveMsgEngage liveMsgEngage) {
        return liveMsgEngageMapper.selectLiveMsgEngageList(liveMsgEngage);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveMsgEngage 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveMsgEngage(LiveMsgEngage liveMsgEngage) {
        return liveMsgEngageMapper.insertLiveMsgEngage(liveMsgEngage);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveMsgEngage 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveMsgEngage(LiveMsgEngage liveMsgEngage) {
        return liveMsgEngageMapper.updateLiveMsgEngage(liveMsgEngage);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveMsgEngageByIds(Long[] ids) {
        return liveMsgEngageMapper.deleteLiveMsgEngageByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveMsgEngageById(Long id) {
        return liveMsgEngageMapper.deleteLiveMsgEngageById(id);
    }
}