package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberGameDatafix;

/**
 * 游戏注单修复Mapper接口
 *
 * @author 77tv
 * @date 2021-06-11
 */
public interface MemberGameDatafixMapper {
	/**
	 * 查询游戏注单修复
	 *
	 * @param id 游戏注单修复ID
	 * @return 游戏注单修复
	 */
	public MemberGameDatafix selectMemberGameDatafixById(String id);

	/**
	 * 查询游戏注单修复列表
	 *
	 * @param memberGameDatafix 游戏注单修复
	 * @return 游戏注单修复集合
	 */
	public List<MemberGameDatafix> selectMemberGameDatafixList(MemberGameDatafix memberGameDatafix);

	/**
	 * 新增游戏注单修复
	 *
	 * @param memberGameDatafix 游戏注单修复
	 * @return 结果
	 */
	public int insertMemberGameDatafix(MemberGameDatafix memberGameDatafix);

	/**
	 * 修改游戏注单修复
	 *
	 * @param memberGameDatafix 游戏注单修复
	 * @return 结果
	 */
	public int updateMemberGameDatafix(MemberGameDatafix memberGameDatafix);

	/**
	 * 删除游戏注单修复
	 *
	 * @param id 游戏注单修复ID
	 * @return 结果
	 */
	public int deleteMemberGameDatafixById(String id);

	/**
	 * 批量删除游戏注单修复
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberGameDatafixByIds(String[] ids );
}
