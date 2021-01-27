package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.ConfigDomain;
import com.qiqilm.server.admin.mapper.ConfigDomainMapper;
import com.qiqilm.server.admin.service.IConfigDomainService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 域名配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ConfigDomainServiceImpl implements IConfigDomainService {
	@Autowired
	private ConfigDomainMapper configDomainMapper;

	/**
	 * 查询域名配置
	 *
	 * @param id 域名配置ID
	 * @return 域名配置
	 */
	@Override
	public ConfigDomain selectConfigDomainById(Long id) {
		return configDomainMapper.selectConfigDomainById(id);
	}

	/**
	 * 查询域名配置列表
	 *
	 * @param configDomain 域名配置
	 * @return 域名配置
	 */
	@Override
	public List<ConfigDomain> selectConfigDomainList(ConfigDomain configDomain) {
		return configDomainMapper.selectConfigDomainList(configDomain);
	}

	/**
	 * 新增域名配置
	 *
	 * @param configDomain 域名配置
	 * @return 结果
	 */
	@Override
	public int insertConfigDomain(ConfigDomain configDomain) {
		configDomain.setCreateTime(DateUtils.getNowDate());
		return configDomainMapper.insertConfigDomain(configDomain);
	}

	/**
	 * 修改域名配置
	 *
	 * @param configDomain 域名配置
	 * @return 结果
	 */
	@Override
	public int updateConfigDomain(ConfigDomain configDomain) {
		configDomain.setUpdateTime(DateUtils.getNowDate());
		return configDomainMapper.updateConfigDomain(configDomain);
	}

	/**
	 * 批量删除域名配置
	 *
	 * @param ids 需要删除的域名配置ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigDomainByIds(Long[] ids) {
		return configDomainMapper.deleteConfigDomainByIds(ids);
	}

	/**
	 * 删除域名配置信息
	 *
	 * @param id 域名配置ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigDomainById(Long id) {
		return configDomainMapper.deleteConfigDomainById(id);
	}
}