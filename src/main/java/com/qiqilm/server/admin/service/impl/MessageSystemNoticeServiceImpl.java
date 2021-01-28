package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MessageSystemNoticeMapper;
import com.qiqilm.server.admin.domain.MessageSystemNotice;
import com.qiqilm.server.admin.service.IMessageSystemNoticeService;

/**
 * 系统公告Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MessageSystemNoticeServiceImpl implements IMessageSystemNoticeService {
    @Autowired
    private MessageSystemNoticeMapper messageSystemNoticeMapper;

    /**
     * 查询系统公告
     *
     * @param id 系统公告ID
     * @return 系统公告
     */
    @Override
    public MessageSystemNotice selectMessageSystemNoticeById(String id) {
        return messageSystemNoticeMapper.selectMessageSystemNoticeById(id);
    }

    /**
     * 查询系统公告列表
     *
     * @param messageSystemNotice 系统公告
     * @return 系统公告
     */
    @Override
    public List<MessageSystemNotice> selectMessageSystemNoticeList(MessageSystemNotice messageSystemNotice) {
        return messageSystemNoticeMapper.selectMessageSystemNoticeList(messageSystemNotice);
    }

    /**
     * 新增系统公告
     *
     * @param messageSystemNotice 系统公告
     * @return 结果
     */
    @Override
    public int insertMessageSystemNotice(MessageSystemNotice messageSystemNotice) {
        return messageSystemNoticeMapper.insertMessageSystemNotice(messageSystemNotice);
    }

    /**
     * 修改系统公告
     *
     * @param messageSystemNotice 系统公告
     * @return 结果
     */
    @Override
    public int updateMessageSystemNotice(MessageSystemNotice messageSystemNotice) {
        return messageSystemNoticeMapper.updateMessageSystemNotice(messageSystemNotice);
    }

    /**
     * 批量删除系统公告
     *
     * @param ids 需要删除的系统公告ID
     * @return 结果
     */
    @Override
    public int deleteMessageSystemNoticeByIds(String[] ids) {
        return messageSystemNoticeMapper.deleteMessageSystemNoticeByIds(ids);
    }

    /**
     * 删除系统公告信息
     *
     * @param id 系统公告ID
     * @return 结果
     */
    @Override
    public int deleteMessageSystemNoticeById(String id) {
        return messageSystemNoticeMapper.deleteMessageSystemNoticeById(id);
    }
}
