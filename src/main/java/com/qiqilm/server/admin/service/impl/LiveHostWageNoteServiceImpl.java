package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveHostWageNoteMapper;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LiveHostWageNoteServiceImpl implements ILiveHostWageNoteService {
    @Autowired
    private LiveHostWageNoteMapper liveHostWageNoteMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveHostWageNote selectLiveHostWageNoteById(Long id) {
        return liveHostWageNoteMapper.selectLiveHostWageNoteById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveHostWageNote> selectLiveHostWageNoteList(LiveHostWageNote liveHostWageNote) {
        return liveHostWageNoteMapper.selectLiveHostWageNoteList(liveHostWageNote);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveHostWageNote(LiveHostWageNote liveHostWageNote) {
        liveHostWageNote.setCreateTime(DateUtils.getNowDate());
        return liveHostWageNoteMapper.insertLiveHostWageNote(liveHostWageNote);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveHostWageNote(LiveHostWageNote liveHostWageNote) {
        return liveHostWageNoteMapper.updateLiveHostWageNote(liveHostWageNote);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageNoteByIds(Long[] ids) {
        return liveHostWageNoteMapper.deleteLiveHostWageNoteByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageNoteById(Long id) {
        return liveHostWageNoteMapper.deleteLiveHostWageNoteById(id);
    }
}
