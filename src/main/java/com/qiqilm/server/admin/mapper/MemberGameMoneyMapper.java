package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberGameMoney;

import java.util.List;

/**
 * 会员游戏上分信息Mapper接口
 *
 * @author 77tv
 * @date 2021-02-04
 */
public interface MemberGameMoneyMapper {
	/**
	 * 查询会员游戏上分信息
	 *
	 * @param id 会员游戏上分信息ID
	 * @return 会员游戏上分信息
	 */
	public MemberGameMoney selectMemberGameMoneyById( String id );

	/**
	 * 查询会员游戏上分信息列表
	 *
	 * @param memberGameMoney 会员游戏上分信息
	 * @return 会员游戏上分信息集合
	 */
	public List<MemberGameMoney> selectMemberGameMoneyList( MemberGameMoney memberGameMoney );

	/**
	 * 新增会员游戏上分信息
	 *
	 * @param memberGameMoney 会员游戏上分信息
	 * @return 结果
	 */
	public int insertMemberGameMoney( MemberGameMoney memberGameMoney );

	/**
	 * 修改会员游戏上分信息
	 *
	 * @param memberGameMoney 会员游戏上分信息
	 * @return 结果
	 */
	public int updateMemberGameMoney( MemberGameMoney memberGameMoney );
}
