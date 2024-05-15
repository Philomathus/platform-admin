package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MessageOnSite;
import org.apache.ibatis.annotations.Param;

/**
 * 站内信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface MessageOnSiteMapper {
	/**
	 * 查询站内信息
	 *
	 * @param id 站内信息ID
	 * @return 站内信息
	 */
	public MessageOnSite selectMessageOnSiteById(String id);

	/**
	 * 查询站内信息列表
	 *
	 * @param messageOnSite 站内信息
	 * @return 站内信息集合
	 */
	public List<MessageOnSite> selectMessageOnSiteList(MessageOnSite messageOnSite);

	/**
	 * 新增站内信息
	 *
	 * @param messageOnSite 站内信息
	 * @return 结果
	 */
	public int insertMessageOnSite(MessageOnSite messageOnSite);


	/**
	 * 修改站内信息
	 *
	 * @param messageOnSite 站内信息
	 * @return 结果
	 */
	public int updateMessageOnSite(MessageOnSite messageOnSite);

	/**
	 * 删除站内信息
	 *
	 * @param id 站内信息ID
	 * @return 结果
	 */
	public int deleteMessageOnSiteById(String id);

	/**
	 * 批量删除站内信息
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMessageOnSiteByIds(String[] ids );
}
