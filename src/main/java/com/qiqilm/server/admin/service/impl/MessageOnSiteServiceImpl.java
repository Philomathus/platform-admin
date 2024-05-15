package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MessageOnSiteMapper;
import com.qiqilm.server.admin.domain.MessageOnSite;
import com.qiqilm.server.admin.service.IMessageOnSiteService;

/**
 * 站内信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MessageOnSiteServiceImpl implements IMessageOnSiteService {
    @Autowired
    private MessageOnSiteMapper messageOnSiteMapper;

    /**
     * 查询站内信息
     *
     * @param id 站内信息ID
     * @return 站内信息
     */
    @Override
    public MessageOnSite selectMessageOnSiteById(String id) {
        return messageOnSiteMapper.selectMessageOnSiteById(id);
    }

    /**
     * 查询站内信息列表
     *
     * @param messageOnSite 站内信息
     * @return 站内信息
     */
    @Override
    public List<MessageOnSite> selectMessageOnSiteList(MessageOnSite messageOnSite) {
        return messageOnSiteMapper.selectMessageOnSiteList(messageOnSite);
    }

    /**
     * 新增站内信息
     *
     * @param messageOnSite 站内信息
     * @return 结果
     */
    @Override
    public int insertMessageOnSite(MessageOnSite messageOnSite) {
        return messageOnSiteMapper.insertMessageOnSite(messageOnSite);
    }

    @Override
    public int insertMultipleMessageOnSite(MessageOnSite messageOnSite) {
        String[] ids = messageOnSite.getToUserId().split( "," );
        messageOnSite.setPubdatetime( new Date() );
        messageOnSite.setReceiverType( "ALL_MEMBER" );
        messageOnSite.setAction( "DIALOG" );
        for ( String id : ids ) {
            messageOnSite.setId( UuidUtil.getRandomUuidWithoutSeparator() );
            messageOnSite.setToUserId( id );
            messageOnSiteMapper.insertMessageOnSite(messageOnSite);
        }
        return 1;
    }

    /**
     * 修改站内信息
     *
     * @param messageOnSite 站内信息
     * @return 结果
     */
    @Override
    public int updateMessageOnSite(MessageOnSite messageOnSite) {
        return messageOnSiteMapper.updateMessageOnSite(messageOnSite);
    }

    /**
     * 批量删除站内信息
     *
     * @param ids 需要删除的站内信息ID
     * @return 结果
     */
    @Override
    public int deleteMessageOnSiteByIds(String[] ids) {
        return messageOnSiteMapper.deleteMessageOnSiteByIds(ids);
    }

    /**
     * 删除站内信息信息
     *
     * @param id 站内信息ID
     * @return 结果
     */
    @Override
    public int deleteMessageOnSiteById(String id) {
        return messageOnSiteMapper.deleteMessageOnSiteById(id);
    }
}
