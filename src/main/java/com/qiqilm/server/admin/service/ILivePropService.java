package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveProp;

/**
 * 礼物列Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ILivePropService {
	/**
	 * 查询礼物列
	 *
	 * @param id 礼物列ID
	 * @return 礼物列
	 */
	public LiveProp selectLivePropById(Long id);

	/**
	 * 查询礼物列列表
	 *
	 * @param liveProp 礼物列
	 * @return 礼物列集合
	 */
	public List<LiveProp> selectLivePropList(LiveProp liveProp);

	/**
	 * 新增礼物列
	 *
	 * @param liveProp 礼物列
	 * @return 结果
	 */
	public int insertLiveProp(LiveProp liveProp);

	/**
	 * 修改礼物列
	 *
	 * @param liveProp 礼物列
	 * @return 结果
	 */
	public int updateLiveProp(LiveProp liveProp);

	/**
	 * 批量删除礼物列
	 *
	 * @param ids 需要删除的礼物列ID
	 * @return 结果
	 */
	public int deleteLivePropByIds(Long[] ids );

	/**
	 * 删除礼物列信息
	 *
	 * @param id 礼物列ID
	 * @return 结果
	 */
	public int deleteLivePropById(Long id);

	List<LiveProp> getList( );
}
