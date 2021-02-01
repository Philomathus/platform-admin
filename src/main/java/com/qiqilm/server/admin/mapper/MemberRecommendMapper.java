package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberRecommend;

import java.util.List;

/**
 * 会员推广记录表Mapper接口
 *
 * @author 77tv
 * @date 2021-02-01
 */
public interface MemberRecommendMapper {
	/**
	 * 查询会员推广记录表
	 *
	 * @param id 会员推广记录表ID
	 * @return 会员推广记录表
	 */
	public MemberRecommend selectMemberRecommendById( String id );

	/**
	 * 查询会员推广记录表列表
	 *
	 * @param memberRecommend 会员推广记录表
	 * @return 会员推广记录表集合
	 */
	public List<MemberRecommend> selectMemberRecommendList( MemberRecommend memberRecommend );

	/**
	 * 新增会员推广记录表
	 *
	 * @param memberRecommend 会员推广记录表
	 * @return 结果
	 */
	public int insertMemberRecommend( MemberRecommend memberRecommend );

	/**
	 * 修改会员推广记录表
	 *
	 * @param memberRecommend 会员推广记录表
	 * @return 结果
	 */
	public int updateMemberRecommend( MemberRecommend memberRecommend );

	/**
	 * 删除会员推广记录表
	 *
	 * @param id 会员推广记录表ID
	 * @return 结果
	 */
	public int deleteMemberRecommendById( String id );

	/**
	 * 批量删除会员推广记录表
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberRecommendByIds( String[] ids );
}