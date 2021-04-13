package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.SmsFailLogMapper;
import com.qiqilm.server.admin.domain.SmsFailLog;
import com.qiqilm.server.admin.service.ISmsFailLogService;

/**
 * 短信发送失败日志Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-13
 */
@Service
public class SmsFailLogServiceImpl implements ISmsFailLogService {
    @Autowired
    private SmsFailLogMapper smsFailLogMapper;

    /**
     * 查询短信发送失败日志
     *
     * @param id 短信发送失败日志ID
     * @return 短信发送失败日志
     */
    @Override
    public SmsFailLog selectSmsFailLogById(Long id) {
        return smsFailLogMapper.selectSmsFailLogById(id);
    }

    /**
     * 查询短信发送失败日志列表
     *
     * @param smsFailLog 短信发送失败日志
     * @return 短信发送失败日志
     */
    @Override
    public List<SmsFailLog> selectSmsFailLogList(SmsFailLog smsFailLog) {
        return smsFailLogMapper.selectSmsFailLogList(smsFailLog);
    }

    /**
     * 新增短信发送失败日志
     *
     * @param smsFailLog 短信发送失败日志
     * @return 结果
     */
    @Override
    public int insertSmsFailLog(SmsFailLog smsFailLog) {
        smsFailLog.setCreateTime(DateUtils.getNowDate());
        return smsFailLogMapper.insertSmsFailLog(smsFailLog);
    }

    /**
     * 修改短信发送失败日志
     *
     * @param smsFailLog 短信发送失败日志
     * @return 结果
     */
    @Override
    public int updateSmsFailLog(SmsFailLog smsFailLog) {
        return smsFailLogMapper.updateSmsFailLog(smsFailLog);
    }

    /**
     * 批量删除短信发送失败日志
     *
     * @param ids 需要删除的短信发送失败日志ID
     * @return 结果
     */
    @Override
    public int deleteSmsFailLogByIds(Long[] ids) {
        return smsFailLogMapper.deleteSmsFailLogByIds(ids);
    }

    /**
     * 删除短信发送失败日志信息
     *
     * @param id 短信发送失败日志ID
     * @return 结果
     */
    @Override
    public int deleteSmsFailLogById(Long id) {
        return smsFailLogMapper.deleteSmsFailLogById(id);
    }
}
