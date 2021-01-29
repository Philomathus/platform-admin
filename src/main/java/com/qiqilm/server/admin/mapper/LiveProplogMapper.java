package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveProplog;

import java.util.List;

/**
 * 用户送礼日志Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface LiveProplogMapper {
	/**
	 * 查询用户送礼日志
	 *
	 * @param id 用户送礼日志ID
	 * @return 用户送礼日志
	 */
	public LiveProplog selectLiveProplogById(Long id);

	/**
	 * 查询用户送礼日志列表
	 *
	 * @param liveProplog 用户送礼日志
	 * @return 用户送礼日志集合
	 */
	public List<LiveProplog> selectLiveProplogList(LiveProplog liveProplog);

	/**
	 * 新增用户送礼日志
	 *
	 * @param liveProplog 用户送礼日志
	 * @return 结果
	 */
	public int insertLiveProplog(LiveProplog liveProplog);

	/**
	 * 修改用户送礼日志
	 *
	 * @param liveProplog 用户送礼日志
	 * @return 结果
	 */
	public int updateLiveProplog(LiveProplog liveProplog);

	/**
	 * 删除用户送礼日志
	 *
	 * @param id 用户送礼日志ID
	 * @return 结果
	 */
	public int deleteLiveProplogById(Long id);

	/**
	 * 批量删除用户送礼日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveProplogByIds(Long[] ids );
}
