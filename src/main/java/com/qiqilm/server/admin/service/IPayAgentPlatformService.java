package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;

import java.util.List;

/**
 * 代付平台Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentPlatformService {
	/**
	 * 查询代付平台
	 *
	 * @param id 代付平台ID
	 * @return 代付平台
	 */
	public PayAgentPlatform selectPayAgentPlatformById( Long id );

	/**
	 * 查询代付平台列表
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 代付平台集合
	 */
	public List<PayAgentPlatform> selectPayAgentPlatformList( PayAgentPlatform payAgentPlatform );

	/**
	 * 新增代付平台
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 结果
	 */
	public int insertPayAgentPlatform( PayAgentPlatform payAgentPlatform );

	/**
	 * 修改代付平台
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 结果
	 */
	public int updatePayAgentPlatform( PayAgentPlatform payAgentPlatform );

	/**
	 * 批量删除代付平台
	 *
	 * @param ids 需要删除的代付平台ID
	 * @return 结果
	 */
	public int deletePayAgentPlatformByIds( Long[] ids );

	/**
	 * 删除代付平台信息
	 *
	 * @param id 代付平台ID
	 * @return 结果
	 */
	public int deletePayAgentPlatformById( Long id );
}
