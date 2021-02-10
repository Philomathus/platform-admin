package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ConfigEnvironment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 环境参数配置Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ConfigEnvironmentMapper {
	/**
	 * 查询环境参数配置
	 *
	 * @param envCode 环境参数配置ID
	 * @return 环境参数配置
	 */
	public ConfigEnvironment selectConfigEnvironmentById( String envCode );

	/**
	 * 查询环境参数配置列表
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 环境参数配置集合
	 */
	public List<ConfigEnvironment> selectConfigEnvironmentList( ConfigEnvironment configEnvironment );

	/**
	 * 新增环境参数配置
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 结果
	 */
	public int insertConfigEnvironment( ConfigEnvironment configEnvironment );

	/**
	 * 修改环境参数配置
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 结果
	 */
	public int updateConfigEnvironment( ConfigEnvironment configEnvironment );

	/**
	 * 删除环境参数配置
	 *
	 * @param envCode 环境参数配置ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentById( String envCode );

	/**
	 * 批量删除环境参数配置
	 *
	 * @param envCodes 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigEnvironmentByIds( String[] envCodes );

	public Integer getTitleIndex( @Param( "title" ) String title, @Param( "code" ) String code );

	public int checkType( String envTitle );

	public int checkCode( String envValue );

	public int checkType2( String envTitle );

	public int checkCode2( String envCode );

    public String getValue();

}
