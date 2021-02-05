package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberGameData;

import java.util.List;

/**
 * 会员注单数据Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface IMemberGameDataService {
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
	 * 批量删除会员注单数据
	 *
	 * @param ids 需要删除的会员注单数据ID
	 * @return 结果
	 */
	public int deleteMemberGameDataByIds(String[] ids );

	/**
	 * 删除会员注单数据信息
	 *
	 * @param id 会员注单数据ID
	 * @return 结果
	 */
	public int deleteMemberGameDataById(String id);

    public AjaxResult getCount(MemberGameData memberGameData);

	AjaxResult getBetData(MemberGameData memberGameData);

}
