package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.SmsFailLog;

/**
 * 短信发送失败日志Service接口
 *
 * @author 77tv
 * @date 2021-04-13
 */
public interface ISmsFailLogService {
	/**
	 * 查询短信发送失败日志
	 *
	 * @param id 短信发送失败日志ID
	 * @return 短信发送失败日志
	 */
	public SmsFailLog selectSmsFailLogById(Long id);

	/**
	 * 查询短信发送失败日志列表
	 *
	 * @param smsFailLog 短信发送失败日志
	 * @return 短信发送失败日志集合
	 */
	public List<SmsFailLog> selectSmsFailLogList(SmsFailLog smsFailLog);

	/**
	 * 新增短信发送失败日志
	 *
	 * @param smsFailLog 短信发送失败日志
	 * @return 结果
	 */
	public int insertSmsFailLog(SmsFailLog smsFailLog);

	/**
	 * 修改短信发送失败日志
	 *
	 * @param smsFailLog 短信发送失败日志
	 * @return 结果
	 */
	public int updateSmsFailLog(SmsFailLog smsFailLog);

	/**
	 * 批量删除短信发送失败日志
	 *
	 * @param ids 需要删除的短信发送失败日志ID
	 * @return 结果
	 */
	public int deleteSmsFailLogByIds(Long[] ids );

	/**
	 * 删除短信发送失败日志信息
	 *
	 * @param id 短信发送失败日志ID
	 * @return 结果
	 */
	public int deleteSmsFailLogById(Long id);
}
