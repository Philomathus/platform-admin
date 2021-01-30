package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.ReqPayJour;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberPayJourService {
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
	 * @param memberPayJour 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberPayJour> selectMemberPayJourList(MemberPayJour memberPayJour);


    public List<RspPayJour> findList( ReqPayJour req );

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
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberPayJourByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberPayJourById(String id);

    /**
     * 数列表
     *
     * @param memberPayJour 会员支付的
     * @return {@link TableDataInfo}
     */
    public Map listCount(ReqPayJour memberPayJour);
}
