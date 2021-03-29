package com.qiqilm.server.admin.service;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.rsp.RspTestAccountProp;

/**
 * 送礼物Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveVideoPropService {

	/**
	 * 查询送礼物列表
	 *
	 * @param liveVideoProp 送礼物
	 * @return 送礼物集合
	 */
	public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp);

	/**
	 * 统计礼物金额
	 */
	LiveVideoProp getCount(LiveVideoProp liveVideoProp);

    List<RspTestAccountProp> testAccountPorpList(LiveVideoProp liveVideoProp);

}
