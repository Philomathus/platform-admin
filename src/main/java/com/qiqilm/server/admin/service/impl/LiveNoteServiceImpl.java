package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveNoteMapper;
import com.qiqilm.server.admin.domain.LiveNote;
import com.qiqilm.server.admin.service.ILiveNoteService;

/**
 * 彩票注单Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-19
 */
@Service
public class LiveNoteServiceImpl implements ILiveNoteService {
    @Autowired
    private LiveNoteMapper liveNoteMapper;

    /**
     * 查询彩票注单
     *
     * @param id 彩票注单ID
     * @return 彩票注单
     */
    @Override
    public LiveNote selectLiveNoteById(String id) {
        return liveNoteMapper.selectLiveNoteById(id);
    }

    /**
     * 查询彩票注单列表
     *
     * @param liveNote 彩票注单
     * @return 彩票注单
     */
    @Override
    public List<LiveNote> selectLiveNoteList(LiveNote liveNote) {
        return liveNoteMapper.selectLiveNoteList(liveNote);
    }


    /**
     * 批量删除彩票注单
     *
     * @param ids 需要删除的彩票注单ID
     * @return 结果
     */
    @Override
    public int deleteLiveNoteByIds(String[] ids) {
        return liveNoteMapper.deleteLiveNoteByIds(ids);
    }

    /**
     * 删除彩票注单信息
     *
     * @param id 彩票注单ID
     * @return 结果
     */
    @Override
    public int deleteLiveNoteById(String id) {
        return liveNoteMapper.deleteLiveNoteById(id);
    }
}
