package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberBcode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MemberBcodeMapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberBcodeMapper {
	/**
	 * 查询MemberBcode
	 *
	 * @param id MemberBcodeID
	 * @return MemberBcode
	 */
	public MemberBcode selectMemberBcodeById(String id);

	/**
	 * 查询MemberBcode列表
	 *
	 * @param memberBcode MemberBcode
	 * @return MemberBcode集合
	 */
	public List<MemberBcode> selectMemberBcodeList(MemberBcode memberBcode);

	/**
	 * 查询MemberBcode列表
	 *
	 * @param memberBcode MemberBcode
	 * @return MemberBcode集合
	 */
	public List<MemberBcode> selectWillBcodeList(MemberBcode memberBcode);

	/**
	 * 新增MemberBcode
	 *
	 * @param memberBcode MemberBcode
	 * @return 结果
	 */
	public int insertMemberBcode(MemberBcode memberBcode);

	/**
	 * 修改MemberBcode
	 *
	 * @param memberBcode MemberBcode
	 * @return 结果
	 */
	public int updateMemberBcode(MemberBcode memberBcode);

	/**
	 * 删除MemberBcode
	 *
	 * @param id MemberBcodeID
	 * @return 结果
	 */
	public int deleteMemberBcodeById(String id);

	/**
	 * 批量删除MemberBcode
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberBcodeByIds(String[] ids );

	MemberBcode getTotalData(MemberBcode memberBcode);

    int countMemberBcodeStatus(@Param("memberId") String memberId);

	void repairMemberInfo(@Param("memberId") String memberId);

    void updateVip(@Param("memberId") String memberId,@Param("vip") Integer vip);
}
