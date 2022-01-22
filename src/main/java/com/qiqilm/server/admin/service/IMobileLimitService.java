package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MobileLimit;

/**
 * 手机号限制Service接口
 *
 * @author 77tv
 * @date 2021-12-09
 */
public interface IMobileLimitService {
	/**
	 * 查询手机号限制
	 *
	 * @param id 手机号限制ID
	 * @return 手机号限制
	 */
	public MobileLimit selectMobileLimitById(Long id);

	/**
	 * 查询手机号限制列表
	 *
	 * @param mobileLimit 手机号限制
	 * @return 手机号限制集合
	 */
	public List<MobileLimit> selectMobileLimitList(MobileLimit mobileLimit);

	/**
	 * 新增手机号限制
	 *
	 * @param mobileLimit 手机号限制
	 * @return 结果
	 */
	public int insertMobileLimit(MobileLimit mobileLimit);

	/**
	 * 修改手机号限制
	 *
	 * @param mobileLimit 手机号限制
	 * @return 结果
	 */
	public int updateMobileLimit(MobileLimit mobileLimit);

	/**
	 * 批量删除手机号限制
	 *
	 * @param ids 需要删除的手机号限制ID
	 * @return 结果
	 */
	public int deleteMobileLimitByIds(Long[] ids );

	/**
	 * 删除手机号限制信息
	 *
	 * @param id 手机号限制ID
	 * @return 结果
	 */
	public int deleteMobileLimitById(Long id);
}
