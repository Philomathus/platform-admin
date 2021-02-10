package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.domain.SysDictData;
import com.qiqilm.server.admin.mapper.ConfigEnvironmentMapper;
import com.qiqilm.server.admin.mapper.SysDictDataMapper;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 环境参数配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ConfigEnvironmentServiceImpl implements IConfigEnvironmentService {
	@Autowired
	private ConfigEnvironmentMapper configEnvironmentMapper;
	@Autowired
	private SysDictDataMapper       dictDataMapper;
	@Autowired
	private SysConfigCacheUtil      sysConfigCacheUtil;

	/**
	 * 查询环境参数配置
	 *
	 * @param envCode 环境参数配置ID
	 * @return 环境参数配置
	 */
	@Override
	public ConfigEnvironment selectConfigEnvironmentById( String envCode ) {
		return configEnvironmentMapper.selectConfigEnvironmentById( envCode );
	}

	/**
	 * 查询环境参数配置列表
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 环境参数配置
	 */
	@Override
	public List<ConfigEnvironment> selectConfigEnvironmentList( ConfigEnvironment configEnvironment ) {
		return configEnvironmentMapper.selectConfigEnvironmentList( configEnvironment );
	}

	/**
	 * 新增环境参数配置
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 结果
	 */
	@Override
	public AjaxResult insertConfigEnvironment( ConfigEnvironment configEnvironment ) {
		if ( "M".equals( configEnvironment.getMenuType() ) ) {
			//判断名称是否存在
			if ( configEnvironmentMapper.checkType( configEnvironment.getEnvTitle() ) != 0 ) {
				return AjaxResult.error( "名称已存在" );
			}
			//判断编码是否存在
			if ( configEnvironmentMapper.checkCode( configEnvironment.getEnvCode() ) != 0 ) {
				return AjaxResult.error( "编码已存在" );
			}
			SysDictData dictData = new SysDictData();
			dictData.setDictSort( configEnvironment.getEnvSort() );
			dictData.setDictLabel( configEnvironment.getEnvTitle() );
			dictData.setDictValue( configEnvironmentMapper.getValue() );
			dictData.setDictType( "config_environment_group" );
			dictData.setStatus( String.valueOf( 0 ) );
			return toAjax( dictDataMapper.insertDictData( dictData ) );
		} else {
			//判断名称是否存在
			if ( configEnvironmentMapper.checkType2( configEnvironment.getEnvTitle() ) != 0 ) {
				return AjaxResult.error( "名称已存在" );
			}
			//判断编码是否存在
			if ( configEnvironmentMapper.checkCode2( configEnvironment.getEnvCode() ) != 0 ) {
				return AjaxResult.error( "编码已存在" );
			}
			int i = configEnvironmentMapper.insertConfigEnvironment( configEnvironment );
			if ( i > 0 ) {
				ConfigEnvironment saveConf =
						configEnvironmentMapper.selectConfigEnvironmentById( configEnvironment.getEnvCode() );
				if ( saveConf.getEnvStatus() == 1 ) {
					sysConfigCacheUtil.setConfCache( saveConf );
				}
			}
			return toAjax( i );
		}
	}

	public AjaxResult toAjax( int rows ) {
		return rows > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	/**
	 * 修改环境参数配置
	 *
	 * @param configEnvironment 环境参数配置
	 * @return 结果
	 */
	@Override
	public int updateConfigEnvironment( ConfigEnvironment configEnvironment ) {
		int i = configEnvironmentMapper.updateConfigEnvironment( configEnvironment );
		if ( i > 0 ) {
			ConfigEnvironment saveConf =
					configEnvironmentMapper.selectConfigEnvironmentById( configEnvironment.getEnvCode() );
			if ( saveConf.getEnvStatus() == 1 ) {
				sysConfigCacheUtil.setConfCache( saveConf );
			}
		}
		return i;
	}

	/**
	 * 批量删除环境参数配置
	 *
	 * @param envCodes 需要删除的环境参数配置ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigEnvironmentByIds( String[] envCodes ) {
		int i = configEnvironmentMapper.deleteConfigEnvironmentByIds( envCodes );
		if ( i > 0 ) {
			sysConfigCacheUtil.deleteCache( envCodes );
		}
		return i;
	}

	/**
	 * 删除环境参数配置信息
	 *
	 * @param envCode 环境参数配置ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigEnvironmentById( String envCode ) {
		int i = configEnvironmentMapper.deleteConfigEnvironmentById( envCode );
		if ( i > 0 ) {
			sysConfigCacheUtil.deleteCache( envCode );
		}
		return i;
	}

	@Override
	public AjaxResult getTitleIndex( String title, String code ) {
		Integer index = configEnvironmentMapper.getTitleIndex( title, code );
		return AjaxResult.success( index );
	}

	@Override
	public void refreshCache() {
		sysConfigCacheUtil.refreshConfCache();
	}
}
