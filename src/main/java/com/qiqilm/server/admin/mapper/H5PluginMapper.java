package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.H5Plugin;

/**
 * h5插件Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface H5PluginMapper {
	/**
	 * 查询h5插件
	 *
	 * @param id h5插件ID
	 * @return h5插件
	 */
	public H5Plugin selectH5PluginById(Long id);

	/**
	 * 查询h5插件列表
	 *
	 * @param h5Plugin h5插件
	 * @return h5插件集合
	 */
	public List<H5Plugin> selectH5PluginList(H5Plugin h5Plugin);

	/**
	 * 新增h5插件
	 *
	 * @param h5Plugin h5插件
	 * @return 结果
	 */
	public int insertH5Plugin(H5Plugin h5Plugin);

	/**
	 * 修改h5插件
	 *
	 * @param h5Plugin h5插件
	 * @return 结果
	 */
	public int updateH5Plugin(H5Plugin h5Plugin);

	/**
	 * 删除h5插件
	 *
	 * @param id h5插件ID
	 * @return 结果
	 */
	public int deleteH5PluginById(Long id);

	/**
	 * 批量删除h5插件
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteH5PluginByIds(Long[] ids );
}
