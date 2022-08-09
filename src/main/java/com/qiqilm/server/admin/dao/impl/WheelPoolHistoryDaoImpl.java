package com.qiqilm.server.admin.dao.impl;


import com.qiqilm.server.admin.dao.WheelPoolHistoryDao;
import com.qiqilm.server.admin.domain.WheelPoolHistory;
import com.qiqilm.server.admin.mapper.WheelPoolHistoryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * wheel pool history dao implementation
 * 接口
 *
 * @author Rajesh
 * @date 2022-07-29
 */
@Component
public class WheelPoolHistoryDaoImpl implements WheelPoolHistoryDao {

    @Resource
    private WheelPoolHistoryMapper wheelPoolHistoryMapper;

    /**查询获取所有轮池历史列表 query get all wheel pool history list data layer*/
    @Override
    public List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory) {
        return wheelPoolHistoryMapper.selectAllWheelPoolHistory(wheelPoolHistory);
    }


    /**
     * 统计总行数和总钱轮池 History dao 实现层
     * - count total rows and total money wheel pool History dao implementation layer
     */
    @Override
    public List<Map<String, Object>> listCount(WheelPoolHistory wheelPoolHistory) {
        return wheelPoolHistoryMapper.listCount(wheelPoolHistory);
    }
}
