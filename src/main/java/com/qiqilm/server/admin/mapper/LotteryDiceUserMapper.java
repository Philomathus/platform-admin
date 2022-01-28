package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LotteryDiceUser;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2022-01-26
 */
public interface LotteryDiceUserMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LotteryDiceUser selectLotteryDiceUserById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param lotteryDiceUser 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LotteryDiceUser> selectLotteryDiceUserList(LotteryDiceUser lotteryDiceUser);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param lotteryDiceUser 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLotteryDiceUser(LotteryDiceUser lotteryDiceUser);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param lotteryDiceUser 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLotteryDiceUser(LotteryDiceUser lotteryDiceUser);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLotteryDiceUserById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLotteryDiceUserByIds(String[] ids );
}
