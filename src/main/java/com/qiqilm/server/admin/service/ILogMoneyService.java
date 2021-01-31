package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LogMoney;

/**
 *  会员资金信息Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface ILogMoneyService {

	/**
	 * 查询 会员资金信息列表
	 *
	 * @param logMoney  会员资金信息
	 * @return  会员资金信息集合
	 */
	public List<LogMoney> selectLogMoneyList(LogMoney logMoney);

}