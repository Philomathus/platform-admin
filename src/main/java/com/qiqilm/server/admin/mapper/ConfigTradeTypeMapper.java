package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ConfigTradeType;

import java.util.List;

/**
 * 资金交易类型Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface ConfigTradeTypeMapper {
	/**
	 * 查询资金交易类型
	 *
	 * @param type 资金交易类型ID
	 * @return 资金交易类型
	 */
	public ConfigTradeType selectConfigTradeTypeById( Long type );

	/**
	 * 查询资金交易类型列表
	 *
	 * @param configTradeType 资金交易类型
	 * @return 资金交易类型集合
	 */
	public List<ConfigTradeType> selectConfigTradeTypeList( ConfigTradeType configTradeType );

	/**
	 * 新增资金交易类型
	 *
	 * @param configTradeType 资金交易类型
	 * @return 结果
	 */
	public int insertConfigTradeType( ConfigTradeType configTradeType );

	/**
	 * 修改资金交易类型
	 *
	 * @param configTradeType 资金交易类型
	 * @return 结果
	 */
	public int updateConfigTradeType( ConfigTradeType configTradeType );

	/**
	 * 删除资金交易类型
	 *
	 * @param type 资金交易类型ID
	 * @return 结果
	 */
	public int deleteConfigTradeTypeById( Long type );

	/**
	 * 批量删除资金交易类型
	 *
	 * @param types 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteConfigTradeTypeByIds( Long[] types );
}