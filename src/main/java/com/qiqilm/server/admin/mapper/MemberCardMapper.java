package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberCardMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberCard selectMemberCardById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberCard 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberCard> selectMemberCardList(MemberCard memberCard);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberCard 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberCard(MemberCard memberCard);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberCard 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberCard(MemberCard memberCard);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberCardById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberCardByIds(String[] ids );

    public List<MemberCard> findList(@Param("memberId") String memberId);

	List<MemberCard> memberCardList(String memberId);

    List<MemberCard> getMemberCardInfo();
}
