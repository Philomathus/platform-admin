package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.domain.H5Plugin;
import com.qiqilm.server.admin.domain.LiveProp;
import com.qiqilm.server.admin.domain.vo.H5PluginVo;
import com.qiqilm.server.admin.mapper.H5PluginMapper;
import com.qiqilm.server.admin.service.IH5PluginService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * h5插件Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class H5PluginServiceImpl implements IH5PluginService {
	@Autowired
	private H5PluginMapper        h5PluginMapper;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;

	@Autowired
	private LiveCacheUtil liveCacheUtil;

	/**
	 * 查询h5插件
	 *
	 * @param id h5插件ID
	 * @return h5插件
	 */
	@Override
	public H5Plugin selectH5PluginById( Long id ) {
		return h5PluginMapper.selectH5PluginById( id );
	}

	/**
	 * 查询h5插件列表
	 *
	 * @param h5Plugin h5插件
	 * @return h5插件
	 */
	@Override
	public List<H5Plugin> selectH5PluginList( H5Plugin h5Plugin ) {
        List<H5Plugin> h5Plugins = h5PluginMapper.selectH5PluginList( h5Plugin );
        if ( !CollectionUtils.isEmpty( h5Plugins ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( H5Plugin plugin : h5Plugins ) {
                if ( StringUtils.isNotBlank( plugin.getIconUrl() ) && !plugin.getIconUrl().startsWith( "http" ) ) {
                    plugin.setIconUrl( domainValue + plugin.getIconUrl() );
                }
            }
        }
        return h5Plugins;
	}

	/**
	 * 新增h5插件
	 *
	 * @param h5Plugin h5插件
	 * @return 结果
	 */
	@Override
	public int insertH5Plugin( H5Plugin h5Plugin ) {
		return h5PluginMapper.insertH5Plugin( h5Plugin );
	}

	/**
	 * 修改h5插件
	 *
	 * @param h5Plugin h5插件
	 * @return 结果
	 */
	@Override
	public int updateH5Plugin( H5Plugin h5Plugin ) {
		int i = h5PluginMapper.updateH5Plugin( h5Plugin );
		H5Plugin record = h5PluginMapper.selectH5PluginById(h5Plugin.getId());
		H5PluginVo vo = new H5PluginVo();
		vo.setType(record.getId().intValue());
		vo.setIcon(record.getIconUrl());
		vo.setLink(record.getConUrl());
		vo.setStatus(record.getStatus());
		vo.setLotteryName(record.getName());
		liveCacheUtil.setH5PluginVo(vo);
		return i;
	}

	/**
	 * 批量删除h5插件
	 *
	 * @param ids 需要删除的h5插件ID
	 * @return 结果
	 */
	@Override
	public int deleteH5PluginByIds( Long[] ids ) {
		return h5PluginMapper.deleteH5PluginByIds( ids );
	}

	/**
	 * 删除h5插件信息
	 *
	 * @param id h5插件ID
	 * @return 结果
	 */
	@Override
	public int deleteH5PluginById( Long id ) {
		return h5PluginMapper.deleteH5PluginById( id );
	}
}
