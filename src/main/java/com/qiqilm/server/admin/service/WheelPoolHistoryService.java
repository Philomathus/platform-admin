package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.WheelPoolHistory;

import java.util.List;

/**
 * 轮池历史服务接口 - wheel pool History service interface
 */
public interface WheelPoolHistoryService {

    /**
     * 查询轮池列表 - wheel pool History
     *
     * @param wheelPoolHistory 轮池一览 list of wheelPool History service
     * @return 轮池列表集合 list of wheelPoolHistory
     */
    List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory);
}
