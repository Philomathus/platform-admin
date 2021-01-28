package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LogMoney;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LogMoneyMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LogMoney selectLogMoneyById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param logMoney 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LogMoney> selectLogMoneyList(LogMoney logMoney);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param logMoney 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLogMoney(LogMoney logMoney);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param logMoney 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLogMoney(LogMoney logMoney);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLogMoneyById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLogMoneyByIds(String[] ids );
}
