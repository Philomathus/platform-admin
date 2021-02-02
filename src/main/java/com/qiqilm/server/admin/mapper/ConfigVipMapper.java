package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigVip;
import com.qiqilm.server.admin.domain.rsp.RspVipSet;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-02-02
 */
public interface ConfigVipMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ConfigVip selectConfigVipById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param configVip 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ConfigVip> selectConfigVipList(ConfigVip configVip);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param configVip 【请填写功能名称】
	 * @return 结果
	 */
	public int insertConfigVip(ConfigVip configVip);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param configVip 【请填写功能名称】
	 * @return 结果
	 */
	public int updateConfigVip(ConfigVip configVip);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteConfigVipById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigVipByIds(String[] ids );

	List<RspVipSet> findListForCache();
}