package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.ServerOss;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * oss文件存储服务配置Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IServerOssService {
	/**
	 * 查询oss文件存储服务配置
	 *
	 * @param id oss文件存储服务配置ID
	 * @return oss文件存储服务配置
	 */
	public ServerOss selectServerOssById( Long id );

	/**
	 * 查询oss文件存储服务配置列表
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return oss文件存储服务配置集合
	 */
	public List<ServerOss> selectServerOssList( ServerOss serverOss);

	/**
	 * 新增oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	public int insertServerOss( ServerOss serverOss );

	/**
	 * 修改oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	public int updateServerOss( ServerOss serverOss );

	/**
	 * 批量删除oss文件存储服务配置
	 *
	 * @param ids 需要删除的oss文件存储服务配置ID
	 * @return 结果
	 */
	public int deleteServerOssByIds( Long[] ids );

	/**
	 * 删除oss文件存储服务配置信息
	 *
	 * @param id oss文件存储服务配置ID
	 * @return 结果
	 */
	public int deleteServerOssById( Long id );

	int effect( long id );

	String uploadInputStream( InputStream inputStream, String fileKey );

	String uploadOssTest( MultipartFile file, String path, long id ) throws IOException;


	void amazonawsUpload(String path, ServerOss serverOss, File newFile);

	void kuaiKuaiYun(MultipartFile file, String fileKey, ServerOss serverOss, File newFile);
}
