package com.qiqilm.server.admin.dao;

import com.qiqilm.server.admin.domain.WheelPoolHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * wheel pool history dao接口
 *
 * @author Rajesh
 * @date 2022-07-29
 */

public interface WheelPoolHistoryDao {

    /**
     * 查询轮池列表 - wheel pool History
     *
     * @param wheelPoolHistory 轮池一览 list of wheelPool history
     * @return 轮池列表集合 list of wheelPool history
     */
    List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory);

    List<Map<String, Object>> listCount(WheelPoolHistory wheelPoolHistory);
}
