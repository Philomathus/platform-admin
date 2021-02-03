package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ConfigEnvironment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ConfigEnvironmentMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param envCode 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigEnvironment selectConfigEnvironmentById(String envCode);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigEnvironment> selectConfigEnvironmentList(ConfigEnvironment configEnvironment);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 结果
	 */
	public int insertConfigEnvironment(ConfigEnvironment configEnvironment);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configEnvironment 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigEnvironment(ConfigEnvironment configEnvironment);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param envCode 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentById(String envCode);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param envCodes 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentByIds(String[] envCodes );

    public Integer getTitleIndex(@Param("title") String title,@Param("code") String code);

    public int checkType(String envTitle);

    public int checkCode(String envValue);

    public int checkType2(String envTitle);

    public int checkCode2(String envCode);
}
