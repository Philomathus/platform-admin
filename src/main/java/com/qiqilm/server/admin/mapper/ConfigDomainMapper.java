package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ConfigDomain;

import java.util.List;

/**
 * 域名配置Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ConfigDomainMapper {
	/**
	 * 查询域名配置
	 *
	 * @param id 域名配置ID
	 * @return 域名配置
	 */
	public ConfigDomain selectConfigDomainById(Long id);

	/**
	 * 查询域名配置列表
	 *
	 * @param configDomain 域名配置
	 * @return 域名配置集合
	 */
	public List<ConfigDomain> selectConfigDomainList(ConfigDomain configDomain);

	/**
	 * 新增域名配置
	 *
	 * @param configDomain 域名配置
	 * @return 结果
	 */
	public int insertConfigDomain(ConfigDomain configDomain);

	/**
	 * 修改域名配置
	 *
	 * @param configDomain 域名配置
	 * @return 结果
	 */
	public int updateConfigDomain(ConfigDomain configDomain);

	/**
	 * 删除域名配置
	 *
	 * @param id 域名配置ID
	 * @return 结果
	 */
	public int deleteConfigDomainById(Long id);

	/**
	 * 批量删除域名配置
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigDomainByIds(Long[] ids );

	List<String> selectDomainsByCode( String code );

	List<ConfigDomain> selectConfigDomainByIds( Long[] ids );

	int existsConfigDomain( ConfigDomain configDomain );
}