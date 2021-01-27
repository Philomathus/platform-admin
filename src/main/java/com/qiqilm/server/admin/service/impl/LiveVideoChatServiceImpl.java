package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoChatMapper;
import com.qiqilm.server.admin.domain.LiveVideoChat;
import com.qiqilm.server.admin.service.ILiveVideoChatService;

/**
 * 会员发言Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveVideoChatServiceImpl implements ILiveVideoChatService {
    @Autowired
    private LiveVideoChatMapper liveVideoChatMapper;

    /**
     * 查询会员发言
     *
     * @param id 会员发言ID
     * @return 会员发言
     */
    @Override
    public LiveVideoChat selectLiveVideoChatById(Long id) {
        return liveVideoChatMapper.selectLiveVideoChatById(id);
    }

    /**
     * 查询会员发言列表
     *
     * @param liveVideoChat 会员发言
     * @return 会员发言
     */
    @Override
    public List<LiveVideoChat> selectLiveVideoChatList(LiveVideoChat liveVideoChat) {
        return liveVideoChatMapper.selectLiveVideoChatList(liveVideoChat);
    }

    /**
     * 新增会员发言
     *
     * @param liveVideoChat 会员发言
     * @return 结果
     */
    @Override
    public int insertLiveVideoChat(LiveVideoChat liveVideoChat) {
        liveVideoChat.setCreateTime(DateUtils.getNowDate());
        return liveVideoChatMapper.insertLiveVideoChat(liveVideoChat);
    }

    /**
     * 修改会员发言
     *
     * @param liveVideoChat 会员发言
     * @return 结果
     */
    @Override
    public int updateLiveVideoChat(LiveVideoChat liveVideoChat) {
        return liveVideoChatMapper.updateLiveVideoChat(liveVideoChat);
    }

    /**
     * 批量删除会员发言
     *
     * @param ids 需要删除的会员发言ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoChatByIds(Long[] ids) {
        return liveVideoChatMapper.deleteLiveVideoChatByIds(ids);
    }

    /**
     * 删除会员发言信息
     *
     * @param id 会员发言ID
     * @return 结果
     */
    @Override
    public int deleteLiveVideoChatById(Long id) {
        return liveVideoChatMapper.deleteLiveVideoChatById(id);
    }
}
