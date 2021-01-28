package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ServerOss;

/**
 * oss文件存储服务配置Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ServerOssMapper {
	/**
	 * 查询oss文件存储服务配置
	 *
	 * @param id oss文件存储服务配置ID
	 * @return oss文件存储服务配置
	 */
	public ServerOss selectServerOssById(Long id);

	/**
	 * 查询oss文件存储服务配置列表
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return oss文件存储服务配置集合
	 */
	public List<ServerOss> selectServerOssList(ServerOss serverOss);

	/**
	 * 新增oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	public int insertServerOss(ServerOss serverOss);

	/**
	 * 修改oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	public int updateServerOss(ServerOss serverOss);

	/**
	 * 删除oss文件存储服务配置
	 *
	 * @param id oss文件存储服务配置ID
	 * @return 结果
	 */
	public int deleteServerOssById(Long id);

	/**
	 * 批量删除oss文件存储服务配置
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteServerOssByIds(Long[] ids );
}
