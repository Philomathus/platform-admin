package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.H5Plugin;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface H5PluginMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public H5Plugin selectH5PluginById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param h5Plugin 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<H5Plugin> selectH5PluginList(H5Plugin h5Plugin);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param h5Plugin 【请填写功能名称】
	 * @return 结果
	 */
	public int insertH5Plugin(H5Plugin h5Plugin);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param h5Plugin 【请填写功能名称】
	 * @return 结果
	 */
	public int updateH5Plugin(H5Plugin h5Plugin);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteH5PluginById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteH5PluginByIds(Long[] ids );
}
