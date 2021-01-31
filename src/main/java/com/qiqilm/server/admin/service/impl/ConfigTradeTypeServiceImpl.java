package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.ConfigTradeType;
import com.qiqilm.server.admin.mapper.ConfigTradeTypeMapper;
import com.qiqilm.server.admin.service.IConfigTradeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资金交易类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class ConfigTradeTypeServiceImpl implements IConfigTradeTypeService {
	@Autowired
	private ConfigTradeTypeMapper configTradeTypeMapper;

	/**
	 * 查询资金交易类型
	 *
	 * @param type 资金交易类型ID
	 * @return 资金交易类型
	 */
	@Override
	public ConfigTradeType selectConfigTradeTypeById( Long type ) {
		return configTradeTypeMapper.selectConfigTradeTypeById( type );
	}

	/**
	 * 查询资金交易类型列表
	 *
	 * @param configTradeType 资金交易类型
	 * @return 资金交易类型
	 */
	@Override
	public List<ConfigTradeType> selectConfigTradeTypeList( ConfigTradeType configTradeType ) {
		return configTradeTypeMapper.selectConfigTradeTypeList( configTradeType );
	}

	/**
	 * 新增资金交易类型
	 *
	 * @param configTradeType 资金交易类型
	 * @return 结果
	 */
	@Override
	public int insertConfigTradeType( ConfigTradeType configTradeType ) {
		return configTradeTypeMapper.insertConfigTradeType( configTradeType );
	}

	/**
	 * 修改资金交易类型
	 *
	 * @param configTradeType 资金交易类型
	 * @return 结果
	 */
	@Override
	public int updateConfigTradeType( ConfigTradeType configTradeType ) {
		return configTradeTypeMapper.updateConfigTradeType( configTradeType );
	}

	/**
	 * 批量删除资金交易类型
	 *
	 * @param types 需要删除的资金交易类型ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigTradeTypeByIds( Long[] types ) {
		return configTradeTypeMapper.deleteConfigTradeTypeByIds( types );
	}

	/**
	 * 删除资金交易类型信息
	 *
	 * @param type 资金交易类型ID
	 * @return 结果
	 */
	@Override
	public int deleteConfigTradeTypeById( Long type ) {
		return configTradeTypeMapper.deleteConfigTradeTypeById( type );
	}
}