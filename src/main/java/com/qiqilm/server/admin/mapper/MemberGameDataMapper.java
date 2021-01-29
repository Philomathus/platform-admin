package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberGameData;

/**
 * 会员注单数据Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface MemberGameDataMapper {
	/**
	 * 查询会员注单数据
	 *
	 * @param id 会员注单数据ID
	 * @return 会员注单数据
	 */
	public MemberGameData selectMemberGameDataById(String id);

	/**
	 * 查询会员注单数据列表
	 *
	 * @param memberGameData 会员注单数据
	 * @return 会员注单数据集合
	 */
	public List<MemberGameData> selectMemberGameDataList(MemberGameData memberGameData);

	/**
	 * 新增会员注单数据
	 *
	 * @param memberGameData 会员注单数据
	 * @return 结果
	 */
	public int insertMemberGameData(MemberGameData memberGameData);

	/**
	 * 修改会员注单数据
	 *
	 * @param memberGameData 会员注单数据
	 * @return 结果
	 */
	public int updateMemberGameData(MemberGameData memberGameData);

	/**
	 * 删除会员注单数据
	 *
	 * @param id 会员注单数据ID
	 * @return 结果
	 */
	public int deleteMemberGameDataById(String id);

	/**
	 * 批量删除会员注单数据
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberGameDataByIds(String[] ids );
}
