package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.ReqPayJour;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;

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
	public MemberPayJour selectMemberPayJourById(String id);

	/**
	 * 查询线上充值信息列表
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 线上充值信息集合
	 */
	public List<MemberPayJour> selectMemberPayJourList(MemberPayJour memberPayJour);

	/**
	 * 新增线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	public int insertMemberPayJour(MemberPayJour memberPayJour);

	/**
	 * 修改线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	public int updateMemberPayJour(MemberPayJour memberPayJour);

	public List<RspPayJour> findList( ReqPayJour req );

    /**
     * 数列表
     *
     * @param memberPayJour 会员支付的
     * @return {@link TableDataInfo}
     */
    public Map listCount(ReqPayJour memberPayJour);

	RspPayJour selectById( String id );
}
