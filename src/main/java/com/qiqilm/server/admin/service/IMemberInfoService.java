package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.vo.PageBO;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;

import java.math.BigDecimal;
import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IMemberInfoService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberInfo selectMemberInfoById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberInfo(MemberInfo memberInfo);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberInfo(MemberInfo memberInfo);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberInfoByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberInfoById(String id);

    RspBase addMemberMoneyOnly(String ip,String userId,BigDecimal money, BigDecimal beatNum, String Mk, String ordermk, String admin_name);

    PageBO<WithdrawReport> withdrawReport(String memberId, Integer page, Integer limit);

    PageBO<MemberCard> findMemberCardPage(String memberid, Integer page, Integer pageSize);

    int updateByPrimaryKeySelective(MemberInfo record);
}
