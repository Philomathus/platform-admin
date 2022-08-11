package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.WheelPoolCacheUtil;
import com.qiqilm.server.admin.dao.WheelPoolHistoryDao;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.WheelPoolHistory;
import com.qiqilm.server.admin.domain.dto.PlatformUser;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.WheelPoolHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 轮池历史服务接口实现 - wheel pool History service interface implementation
 */
@Service
public class wheelPoolHistoryServiceImpl implements WheelPoolHistoryService {

    @Autowired
    private WheelPoolHistoryDao wheelPoolHistoryDao;

    @Resource
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private WheelPoolCacheUtil wheelPoolCacheUtil;

    /**
     * 查询轮池列表 - wheel pool History service implementation layer
     */
    @Override
    public List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory) {
        List<WheelPoolHistory> wheelPoolHistoryList = wheelPoolHistoryDao.selectAllWheelPoolHistory(wheelPoolHistory);
        Set<String> memberIds = wheelPoolHistoryList.stream().map(WheelPoolHistory::getMemberId).collect(Collectors.toSet());
        if(!memberIds.isEmpty()){
            List<MemberInfo> memberInfos = memberInfoMapper.selectStatusByIds(memberIds);
            for (WheelPoolHistory history : wheelPoolHistoryList) {
                for (MemberInfo memberInfo : memberInfos) {
                    if (history.getMemberId().equals(memberInfo.getId())) {
                        history.setMemberStatus(memberInfo.getStatus());
                    }
                }
            }
        }

        return wheelPoolHistoryList;
    }

    @Override
    public List<PlatformUser> wheelPoolLotteryCacheList() {
        List<PlatformUser> list = wheelPoolCacheUtil.getLotteryList();
        if (list == null) {
            throw new BusinessException("数据不可用");
        }
        return list;
    }

    /**
     * 计数总行数和总钱轮池历史服务实现层
     * - count total rows and total money wheel pool History service implementation layer
     */
    @Override
    public Map<String, Object> listCount(WheelPoolHistory wheelPoolHistory) {
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> dataList = wheelPoolHistoryDao.listCount(wheelPoolHistory);

        for (Map<String, Object> resultMap : dataList) {
            if (resultMap.get("statusStr").equals("n")) {
                map.put("totalPeopleCount", resultMap.get("totalCount"));
                map.put("totalCountMoney", resultMap.get("totalMoney"));
            } else {
                map.put("testTotalPeoples", resultMap.get("totalCount"));
                map.put("testTotalMoney", resultMap.get("totalMoney"));
            }
        }
        return map;
    }
}
