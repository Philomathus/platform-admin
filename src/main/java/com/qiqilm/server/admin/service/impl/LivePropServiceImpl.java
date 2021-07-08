package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.LiveVideoCacheUtil;
import com.qiqilm.server.admin.domain.LiveProp;
import com.qiqilm.server.admin.mapper.LivePropMapper;
import com.qiqilm.server.admin.service.ILivePropService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 礼物列Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LivePropServiceImpl implements ILivePropService {
	@Autowired
	private LivePropMapper        livePropMapper;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;
	@Autowired
	private  LiveVideoCacheUtil liveVideoCacheUtil;

	/**
	 * 查询礼物列
	 *
	 * @param id 礼物列ID
	 * @return 礼物列
	 */
	@Override
	public LiveProp selectLivePropById( Long id ) {
		return livePropMapper.selectLivePropById( id );
	}

	/**
	 * 查询礼物列列表
	 *
	 * @param liveProp 礼物列
	 * @return 礼物列
	 */
	@Override
	public List<LiveProp> selectLivePropList( LiveProp liveProp ) {
		List<LiveProp> liveProps = livePropMapper.selectLivePropList( liveProp );
		if ( !CollectionUtils.isEmpty( liveProps ) ) {
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			for ( LiveProp prop : liveProps ) {
				if ( StringUtils.isNotBlank( prop.getIcon() ) && !prop.getIcon().startsWith( "http" ) ) {
					prop.setIcon( domainValue + prop.getIcon() );
				}
			}
		}
		return liveProps;
	}

	/**
	 * 新增礼物列
	 *
	 * @param liveProp 礼物列
	 * @return 结果
	 */
	@Override
	public int insertLiveProp( LiveProp liveProp ) {
		int i = livePropMapper.insertLiveProp(liveProp);
		liveVideoCacheUtil.setLiveVideoCach(liveProp.getType());
		return i;

	}

	/**
	 * 修改礼物列
	 *
	 * @param liveProp 礼物列
	 * @return 结果
	 */
	@Override
	public int updateLiveProp( LiveProp liveProp ) {
		int i=livePropMapper.updateLiveProp( liveProp );
		String type;
		if (Strings.isBlank(liveProp.getType())){
			LiveProp liveProp1 = livePropMapper.selectLivePropById(liveProp.getId());
			type=liveProp1.getType();
		}else {
			type=liveProp.getType();
		}
		liveVideoCacheUtil.setLiveVideoCach(type);
		return i;
	}

	/**
	 * 批量删除礼物列
	 *
	 * @param ids 需要删除的礼物列ID
	 * @return 结果
	 */
	@Override
	public int deleteLivePropByIds( Long[] ids ) {
		return livePropMapper.deleteLivePropByIds( ids );
	}

	/**
	 * 删除礼物列信息
	 *
	 * @param id 礼物列ID
	 * @return 结果
	 */
	@Override
	public int deleteLivePropById( Long id ) {
		LiveProp liveProp = livePropMapper.selectLivePropById(id);
		int i= livePropMapper.deleteLivePropById( id );
		liveVideoCacheUtil.setLiveVideoCach(liveProp.getType());
		return i;
	}

	@Override
	public List<LiveProp> getList() {
		return livePropMapper.getList();
	}
}
