package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.CommonProblem;

/**
 * 常见问题Service接口
 *
 * @author 77tv
 * @date 2021-02-07
 */
public interface ICommonProblemService {
	/**
	 * 查询常见问题
	 *
	 * @param id 常见问题ID
	 * @return 常见问题
	 */
	public CommonProblem selectCommonProblemById(String id);

	/**
	 * 查询常见问题列表
	 *
	 * @param commonProblem 常见问题
	 * @return 常见问题集合
	 */
	public List<CommonProblem> selectCommonProblemList(CommonProblem commonProblem);

	/**
	 * 新增常见问题
	 *
	 * @param commonProblem 常见问题
	 * @return 结果
	 */
	public int insertCommonProblem(CommonProblem commonProblem);

	/**
	 * 修改常见问题
	 *
	 * @param commonProblem 常见问题
	 * @return 结果
	 */
	public int updateCommonProblem(CommonProblem commonProblem);

	/**
	 * 批量删除常见问题
	 *
	 * @param ids 需要删除的常见问题ID
	 * @return 结果
	 */
	public int deleteCommonProblemByIds(String[] ids );

	/**
	 * 删除常见问题信息
	 *
	 * @param id 常见问题ID
	 * @return 结果
	 */
	public int deleteCommonProblemById(String id);
}
