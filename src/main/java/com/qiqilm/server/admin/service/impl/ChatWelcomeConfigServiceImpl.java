package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ChatWelcomeConfigMapper;
import com.qiqilm.server.admin.domain.ChatWelcomeConfig;
import com.qiqilm.server.admin.service.IChatWelcomeConfigService;

/**
 * 代充人欢迎语配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-24
 */
@Service
public class ChatWelcomeConfigServiceImpl implements IChatWelcomeConfigService {
    @Autowired
    private ChatWelcomeConfigMapper chatWelcomeConfigMapper;

    /**
     * 查询代充人欢迎语配置
     *
     * @param id 代充人欢迎语配置ID
     * @return 代充人欢迎语配置
     */
    @Override
    public ChatWelcomeConfig selectChatWelcomeConfigById(Long id) {
        return chatWelcomeConfigMapper.selectChatWelcomeConfigById(id);
    }

    @Override
    public int selectChatWelcomeConfigByAgentId(Long agentId) {
        return chatWelcomeConfigMapper.selectChatWelcomeConfigByAgentId(agentId);
    }

    /**
     * 查询代充人欢迎语配置列表
     *
     * @param chatWelcomeConfig 代充人欢迎语配置
     * @return 代充人欢迎语配置
     */
    @Override
    public List<ChatWelcomeConfig> selectChatWelcomeConfigList(ChatWelcomeConfig chatWelcomeConfig) {
        return chatWelcomeConfigMapper.selectChatWelcomeConfigList(chatWelcomeConfig);
    }

    /**
     * 新增代充人欢迎语配置
     *
     * @param chatWelcomeConfig 代充人欢迎语配置
     * @return 结果
     */
    @Override
    public int insertChatWelcomeConfig(ChatWelcomeConfig chatWelcomeConfig) {
        chatWelcomeConfig.setCreateTime(DateUtils.getNowDate());
        return chatWelcomeConfigMapper.insertChatWelcomeConfig(chatWelcomeConfig);
    }

    /**
     * 修改代充人欢迎语配置
     *
     * @param chatWelcomeConfig 代充人欢迎语配置
     * @return 结果
     */
    @Override
    public int updateChatWelcomeConfig(ChatWelcomeConfig chatWelcomeConfig) {
        chatWelcomeConfig.setUpdateTime(DateUtils.getNowDate());
        return chatWelcomeConfigMapper.updateChatWelcomeConfig(chatWelcomeConfig);
    }

    /**
     * 批量删除代充人欢迎语配置
     *
     * @param ids 需要删除的代充人欢迎语配置ID
     * @return 结果
     */
    @Override
    public int deleteChatWelcomeConfigByIds(Long[] ids) {
        return chatWelcomeConfigMapper.deleteChatWelcomeConfigByIds(ids);
    }

    /**
     * 删除代充人欢迎语配置信息
     *
     * @param id 代充人欢迎语配置ID
     * @return 结果
     */
    @Override
    public int deleteChatWelcomeConfigById(Long id) {
        return chatWelcomeConfigMapper.deleteChatWelcomeConfigById(id);
    }
}
