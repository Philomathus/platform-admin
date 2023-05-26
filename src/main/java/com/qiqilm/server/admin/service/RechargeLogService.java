package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.RechargeLog;

import java.util.List;

/**
 * 充值日志服务 interface
 *
 * @author Rajesh
 * @date 2023-05-20
 */
public interface RechargeLogService {

    /**
     * 充值日志列表 - recharge log list
     *
     * @param rechargeLog  - list of recharge log
     * @return 返回充值日志列表 - ist of recharge log
     */
    List<RechargeLog> selectAllRechargeLog( RechargeLog rechargeLog);

}
