package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.MemberPayJour;

import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberPayJourService {
	/**
	 * 查询线上充值信息
	 *
	 * @param id 线上充值信息ID
	 * @return 线上充值信息
	 */
	public MemberPayJour selectMemberPayJourById( String id );

	/**
	 * 查询线上充值信息列表
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 线上充值信息集合
	 */
	public List<MemberPayJour> selectMemberPayJourList( MemberPayJour memberPayJour );

	/**
	 * 数列表
	 *
	 * @param memberPayJour 会员支付的
	 * @return {@link TableDataInfo}
	 */
	public Map listCount( MemberPayJour memberPayJour );
}
