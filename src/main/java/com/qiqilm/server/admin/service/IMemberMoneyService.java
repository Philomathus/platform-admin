package com.qiqilm.server.admin.service;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.MemberMoney;

/**
 * 派送彩金暂存表Service接口
 *
 * @author 77tv
 * @date 2022-02-09
 */
public interface IMemberMoneyService {
	/**
	 * 查询派送彩金暂存表
	 *
	 * @param memberId 派送彩金暂存表ID
	 * @return 派送彩金暂存表
	 */
	public MemberMoney selectMemberMoneyById(String memberId);

	/**
	 * 查询派送彩金暂存表列表
	 *
	 * @param memberMoney 派送彩金暂存表
	 * @return 派送彩金暂存表集合
	 */
	public List<MemberMoney> selectMemberMoneyList(MemberMoney memberMoney);

	/**
	 * 新增派送彩金暂存表
	 *
	 * @param memberMoney 派送彩金暂存表
	 * @return 结果
	 */
	public int insertMemberMoney(MemberMoney memberMoney);
	AjaxResult starSend(MemberMoney memberMoney) throws Exception;

	/**
	 * 修改派送彩金暂存表
	 *
	 * @param memberMoney 派送彩金暂存表
	 * @return 结果
	 */
	public int updateMemberMoney(MemberMoney memberMoney);

	/**
	 * 批量删除派送彩金暂存表
	 *
	 * @param memberIds 需要删除的派送彩金暂存表ID
	 * @return 结果
	 */
	public int deleteMemberMoneyByIds(String[] memberIds );

	/**
	 * 删除派送彩金暂存表信息
	 *
	 * @param memberId 派送彩金暂存表ID
	 * @return 结果
	 */
	public int deleteMemberMoneyById(String memberId);

	/**
	 *批量清理临时支付表 clear the temporary payout table in batches
	 */
     public Integer clear();

	 BigDecimal countMoney();
}
