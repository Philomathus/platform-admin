package com.qiqilm.server.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiqilm.server.admin.cache.ServerOssCacheUtil;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.mapper.ServerOssMapper;
import com.qiqilm.server.admin.service.IServerOssService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * oss文件存储服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerOssServiceImpl implements IServerOssService {
	@Autowired
	private ServerOssMapper    serverOssMapper;
	@Autowired
	private ServerOssCacheUtil serverOssCacheUtil;

	/**
	 * 查询oss文件存储服务配置
	 *
	 * @param id oss文件存储服务配置ID
	 * @return oss文件存储服务配置
	 */
	@Override
	public ServerOss selectServerOssById( Long id ) {
		return serverOssMapper.selectServerOssById( id );
	}

	/**
	 * 查询oss文件存储服务配置列表
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return oss文件存储服务配置
	 */
	@Override
	public List<ServerOss> selectServerOssList( ServerOss serverOss ) {
		return serverOssMapper.selectServerOssList( serverOss );
	}

	/**
	 * 新增oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	@Override
	public int insertServerOss( ServerOss serverOss ) {
		serverOss.setCreateTime( DateUtils.getNowDate() );
		return serverOssMapper.insertServerOss( serverOss );
	}

	/**
	 * 修改oss文件存储服务配置
	 *
	 * @param serverOss oss文件存储服务配置
	 * @return 结果
	 */
	@Override
	public int updateServerOss( ServerOss serverOss ) {
		serverOss.setUpdateTime( DateUtils.getNowDate() );
		int i = serverOssMapper.updateServerOss( serverOss );
		if ( i > 0 ) {
			serverOssCacheUtil.clear();
		}
		return i;
	}

	/**
	 * 批量删除oss文件存储服务配置
	 *
	 * @param ids 需要删除的oss文件存储服务配置ID
	 * @return 结果
	 */
	@Override
	public int deleteServerOssByIds( Long[] ids ) {
		return serverOssMapper.deleteServerOssByIds( ids );
	}

	/**
	 * 删除oss文件存储服务配置信息
	 *
	 * @param id oss文件存储服务配置ID
	 * @return 结果
	 */
	@Override
	public int deleteServerOssById( Long id ) {
		return serverOssMapper.deleteServerOssById( id );
	}

	@Override
	@Transactional( rollbackFor = Exception.class )
	public int effect( long id ) {
		//只允许激活一个
		List<ServerOss> serverOssList = serverOssMapper.selectServerOssList( null );
		for ( ServerOss serverOss : serverOssList ) {
			ServerOss update = new ServerOss();
			update.setId( serverOss.getId() );
			update.setIsEffect( 0 );
			serverOssMapper.updateServerOss( update );
		}
		ServerOss update = new ServerOss();
		update.setId( id );
		update.setIsEffect( 1 );
		int i = serverOssMapper.updateServerOss( update );
		if ( i > 0 ) {
			serverOssCacheUtil.clear();
		}
		return i;
	}

	@Override
	public String uploadInputStream( InputStream inputStream, String fileKey ) {
		ServerOss serverOss = serverOssCacheUtil.getAllValue();
		return this.uploadOss( inputStream, fileKey, serverOss );
	}

	@Override
	public String uploadOssTest( InputStream inputStream, String fileKey, long id ) {
		ServerOss serverOss = serverOssMapper.selectServerOssById( id );
		return this.uploadOss( inputStream, fileKey, serverOss );
	}

	private String uploadOss( InputStream inputStream, String fileKey, ServerOss serverOss ) {
		// 创建OSSClient实例
		OSS ossClient = new OSSClientBuilder().build( serverOss.getEndpoint(), serverOss.getAccessKey(),
				serverOss.getAccessSecret() );
		// 上传文件流
		ossClient.putObject( serverOss.getBucket(), fileKey, inputStream );
		// 关闭client
		ossClient.shutdown();
		return "/" + fileKey;
	}
}
