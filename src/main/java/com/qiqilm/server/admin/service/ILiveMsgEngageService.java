package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveMsgEngage;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-03-22
 */
public interface ILiveMsgEngageService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveMsgEngage selectLiveMsgEngageById(Integer id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveMsgEngage 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveMsgEngage> selectLiveMsgEngageList(LiveMsgEngage liveMsgEngage);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveMsgEngage 【请填写功能名称】
	 * @return 结果
	 */
	AjaxResult insertLiveMsgEngage(LiveMsgEngage liveMsgEngage);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveMsgEngage 【请填写功能名称】
	 * @return 结果
	 */
	AjaxResult updateLiveMsgEngage(LiveMsgEngage liveMsgEngage);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveMsgEngageByIds(Integer[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveMsgEngageById(Integer id);
}