package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LiveLimitMsg;

/**
 * //昵称限制Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ILiveLimitMsgService {
	/**
	 * 查询//昵称限制
	 *
	 * @param id //昵称限制ID
	 * @return //昵称限制
	 */
	public String selectLiveLimitMsgById();


	/**
	 * 修改//昵称限制
	 *
	 * @param liveLimitMsg //昵称限制
	 * @return 结果
	 */
	public int updateLiveLimitMsg( LiveLimitMsg liveLimitMsg);
}