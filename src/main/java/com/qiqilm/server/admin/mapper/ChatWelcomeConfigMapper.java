package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ChatWelcomeConfig;

/**
 * 代充人欢迎语配置Mapper接口
 *
 * @author 77tv
 * @date 2021-09-24
 */
public interface ChatWelcomeConfigMapper {
	/**
	 * 查询代充人欢迎语配置
	 *
	 * @param id 代充人欢迎语配置ID
	 * @return 代充人欢迎语配置
	 */
	public ChatWelcomeConfig selectChatWelcomeConfigById(Long id);
	public ChatWelcomeConfig selectChatWelcomeConfigByAgentId(Long id);

	/**
	 * 查询代充人欢迎语配置列表
	 *
	 * @param chatWelcomeConfig 代充人欢迎语配置
	 * @return 代充人欢迎语配置集合
	 */
	public List<ChatWelcomeConfig> selectChatWelcomeConfigList(ChatWelcomeConfig chatWelcomeConfig);

	/**
	 * 新增代充人欢迎语配置
	 *
	 * @param chatWelcomeConfig 代充人欢迎语配置
	 * @return 结果
	 */
	public int insertChatWelcomeConfig(ChatWelcomeConfig chatWelcomeConfig);

	/**
	 * 修改代充人欢迎语配置
	 *
	 * @param chatWelcomeConfig 代充人欢迎语配置
	 * @return 结果
	 */
	public int updateChatWelcomeConfig(ChatWelcomeConfig chatWelcomeConfig);

	/**
	 * 删除代充人欢迎语配置
	 *
	 * @param id 代充人欢迎语配置ID
	 * @return 结果
	 */
	public int deleteChatWelcomeConfigById(Long id);

	/**
	 * 批量删除代充人欢迎语配置
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteChatWelcomeConfigByIds(Long[] ids );
}
