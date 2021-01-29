package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LogGameOrder;

/**
 * 会员上下分Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface ILogGameOrderService {
	/**
	 * 查询会员上下分
	 *
	 * @param id 会员上下分ID
	 * @return 会员上下分
	 */
	public LogGameOrder selectLogGameOrderById(String id);

	/**
	 * 查询会员上下分列表
	 *
	 * @param logGameOrder 会员上下分
	 * @return 会员上下分集合
	 */
	public List<LogGameOrder> selectLogGameOrderList(LogGameOrder logGameOrder);

	/**
	 * 新增会员上下分
	 *
	 * @param logGameOrder 会员上下分
	 * @return 结果
	 */
	public int insertLogGameOrder(LogGameOrder logGameOrder);

	/**
	 * 修改会员上下分
	 *
	 * @param logGameOrder 会员上下分
	 * @return 结果
	 */
	public int updateLogGameOrder(LogGameOrder logGameOrder);

	/**
	 * 批量删除会员上下分
	 *
	 * @param ids 需要删除的会员上下分ID
	 * @return 结果
	 */
	public int deleteLogGameOrderByIds(String[] ids );

	/**
	 * 删除会员上下分信息
	 *
	 * @param id 会员上下分ID
	 * @return 结果
	 */
	public int deleteLogGameOrderById(String id);
}
