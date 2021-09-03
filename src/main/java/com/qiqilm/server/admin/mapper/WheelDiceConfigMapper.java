package com.qiqilm.server.admin.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.domain.WheelDiceConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-09-01
 */
public interface WheelDiceConfigMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public WheelDiceConfig selectWheelDiceConfigById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param wheelDiceConfig 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<WheelDiceConfig> selectWheelDiceConfigList(WheelDiceConfig wheelDiceConfig);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param wheelDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int insertWheelDiceConfig(WheelDiceConfig wheelDiceConfig);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param wheelDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int updateWheelDiceConfig(WheelDiceConfig wheelDiceConfig);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteWheelDiceConfigById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelDiceConfigByIds(Long[] ids );

	Integer selectWheelDiceBycash(@Param("cash") BigDecimal cash);
}
