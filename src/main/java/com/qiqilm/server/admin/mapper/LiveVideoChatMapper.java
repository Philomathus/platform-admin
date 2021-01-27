package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveVideoChat;

/**
 * 会员发言Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveVideoChatMapper {
	/**
	 * 查询会员发言
	 *
	 * @param id 会员发言ID
	 * @return 会员发言
	 */
	public LiveVideoChat selectLiveVideoChatById(Long id);

	/**
	 * 查询会员发言列表
	 *
	 * @param liveVideoChat 会员发言
	 * @return 会员发言集合
	 */
	public List<LiveVideoChat> selectLiveVideoChatList(LiveVideoChat liveVideoChat);

	/**
	 * 新增会员发言
	 *
	 * @param liveVideoChat 会员发言
	 * @return 结果
	 */
	public int insertLiveVideoChat(LiveVideoChat liveVideoChat);

	/**
	 * 修改会员发言
	 *
	 * @param liveVideoChat 会员发言
	 * @return 结果
	 */
	public int updateLiveVideoChat(LiveVideoChat liveVideoChat);

	/**
	 * 删除会员发言
	 *
	 * @param id 会员发言ID
	 * @return 结果
	 */
	public int deleteLiveVideoChatById(Long id);

	/**
	 * 批量删除会员发言
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveVideoChatByIds(Long[] ids );
}
