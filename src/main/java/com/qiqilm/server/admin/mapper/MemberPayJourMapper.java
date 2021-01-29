package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.ReqPayJour;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberPayJourMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberPayJour selectMemberPayJourById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param req 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberPayJour> selectMemberPayJourList( @Param( "req" ) MemberPayJour req);

	List<RspPayJour> findList( @Param( "req" ) ReqPayJour req );

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberPayJour 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberPayJour(MemberPayJour memberPayJour);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberPayJour 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberPayJour(MemberPayJour memberPayJour);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberPayJourById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberPayJourByIds(String[] ids );
}
