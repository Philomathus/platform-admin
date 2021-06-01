package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
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
	 * 查询会员注单数据列表
	 *
	 * @param reqMemberGameData 会员注单数据
	 * @return 会员注单数据集合
	 */
	public List<RspMemberGameData> selectMemberGameDataList(ReqMemberGameData reqMemberGameData);

    public RspMemberGameData getCount(ReqMemberGameData reqMemberGameData);

	AjaxResult getBetData(MemberGameData memberGameData);

	AjaxResult GameKYResult(MemberGameData memberGameData);


}
