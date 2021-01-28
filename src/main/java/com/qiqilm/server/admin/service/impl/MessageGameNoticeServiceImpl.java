package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MessageGameNoticeMapper;
import com.qiqilm.server.admin.domain.MessageGameNotice;
import com.qiqilm.server.admin.service.IMessageGameNoticeService;

/**
 * 游戏公告Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MessageGameNoticeServiceImpl implements IMessageGameNoticeService {
    @Autowired
    private MessageGameNoticeMapper messageGameNoticeMapper;

    /**
     * 查询游戏公告
     *
     * @param id 游戏公告ID
     * @return 游戏公告
     */
    @Override
    public MessageGameNotice selectMessageGameNoticeById(String id) {
        return messageGameNoticeMapper.selectMessageGameNoticeById(id);
    }

    /**
     * 查询游戏公告列表
     *
     * @param messageGameNotice 游戏公告
     * @return 游戏公告
     */
    @Override
    public List<MessageGameNotice> selectMessageGameNoticeList(MessageGameNotice messageGameNotice) {
        return messageGameNoticeMapper.selectMessageGameNoticeList(messageGameNotice);
    }

    /**
     * 新增游戏公告
     *
     * @param messageGameNotice 游戏公告
     * @return 结果
     */
    @Override
    public int insertMessageGameNotice(MessageGameNotice messageGameNotice) {
        return messageGameNoticeMapper.insertMessageGameNotice(messageGameNotice);
    }

    /**
     * 修改游戏公告
     *
     * @param messageGameNotice 游戏公告
     * @return 结果
     */
    @Override
    public int updateMessageGameNotice(MessageGameNotice messageGameNotice) {
        return messageGameNoticeMapper.updateMessageGameNotice(messageGameNotice);
    }

    /**
     * 批量删除游戏公告
     *
     * @param ids 需要删除的游戏公告ID
     * @return 结果
     */
    @Override
    public int deleteMessageGameNoticeByIds(String[] ids) {
        return messageGameNoticeMapper.deleteMessageGameNoticeByIds(ids);
    }

    /**
     * 删除游戏公告信息
     *
     * @param id 游戏公告ID
     * @return 结果
     */
    @Override
    public int deleteMessageGameNoticeById(String id) {
        return messageGameNoticeMapper.deleteMessageGameNoticeById(id);
    }
}
