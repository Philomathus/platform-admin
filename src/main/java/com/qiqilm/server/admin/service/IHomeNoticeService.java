package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.HomeNotice;

/**
 * 系统公告Service接口
 *
 * @author 77tv
 * @date 2021-02-07
 */
public interface IHomeNoticeService {
	/**
	 * 查询系统公告
	 *
	 * @param id 系统公告ID
	 * @return 系统公告
	 */
	public HomeNotice selectHomeNoticeById(String id);

	/**
	 * 查询系统公告列表
	 *
	 * @param homeNotice 系统公告
	 * @return 系统公告集合
	 */
	public List<HomeNotice> selectHomeNoticeList(HomeNotice homeNotice);

	/**
	 * 新增系统公告
	 *
	 * @param homeNotice 系统公告
	 * @return 结果
	 */
	public int insertHomeNotice(HomeNotice homeNotice);

	/**
	 * 修改系统公告
	 *
	 * @param homeNotice 系统公告
	 * @return 结果
	 */
	public int updateHomeNotice(HomeNotice homeNotice);

	/**
	 * 批量删除系统公告
	 *
	 * @param ids 需要删除的系统公告ID
	 * @return 结果
	 */
	public int deleteHomeNoticeByIds(String[] ids );

	/**
	 * 删除系统公告信息
	 *
	 * @param id 系统公告ID
	 * @return 结果
	 */
	public int deleteHomeNoticeById(String id);
}
