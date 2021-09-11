package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ChatComplaintMapper;
import com.qiqilm.server.admin.domain.ChatComplaint;
import com.qiqilm.server.admin.service.IChatComplaintService;

/**
 * 客服投诉Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-10
 */
@Service
public class ChatComplaintServiceImpl implements IChatComplaintService {
    @Autowired
    private ChatComplaintMapper chatComplaintMapper;
    @Autowired
    private TokenService       tokenService;

    /**
     * 查询客服投诉
     *
     * @param id 客服投诉ID
     * @return 客服投诉
     */
    @Override
    public ChatComplaint selectChatComplaintById(Long id) {
        return chatComplaintMapper.selectChatComplaintById(id);
    }

    /**
     * 查询客服投诉列表
     *
     * @param chatComplaint 客服投诉
     * @return 客服投诉
     */
    @Override
    public List<ChatComplaint> selectChatComplaintList(ChatComplaint chatComplaint) {
        return chatComplaintMapper.selectChatComplaintList(chatComplaint);
    }

    /**
     * 新增客服投诉
     *
     * @param chatComplaint 客服投诉
     * @return 结果
     */
    @Override
    public int insertChatComplaint(ChatComplaint chatComplaint) {
        chatComplaint.setCreateTime(DateUtils.getNowDate());
        return chatComplaintMapper.insertChatComplaint(chatComplaint);
    }

    /**
     * 修改客服投诉
     *
     * @param chatComplaint 客服投诉
     * @return 结果
     */
    @Override
    public int updateChatComplaint(ChatComplaint chatComplaint) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        chatComplaint.setApprover(userName);
        chatComplaint.setProcessingTime(new Date());
        return chatComplaintMapper.updateChatComplaint(chatComplaint);
    }

    /**
     * 批量删除客服投诉
     *
     * @param ids 需要删除的客服投诉ID
     * @return 结果
     */
    @Override
    public int deleteChatComplaintByIds(Long[] ids) {
        return chatComplaintMapper.deleteChatComplaintByIds(ids);
    }

    /**
     * 删除客服投诉信息
     *
     * @param id 客服投诉ID
     * @return 结果
     */
    @Override
    public int deleteChatComplaintById(Long id) {
        return chatComplaintMapper.deleteChatComplaintById(id);
    }
}
