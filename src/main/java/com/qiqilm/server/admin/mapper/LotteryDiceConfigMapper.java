package com.qiqilm.server.admin.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.domain.LotteryDiceConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2022-01-26
 */
public interface LotteryDiceConfigMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LotteryDiceConfig selectLotteryDiceConfigById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LotteryDiceConfig> selectLotteryDiceConfigList(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param lotteryDiceConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLotteryDiceConfigById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLotteryDiceConfigByIds(Long[] ids );

	Integer selectWheelDiceBycash(@Param("cash") BigDecimal cash);
}
