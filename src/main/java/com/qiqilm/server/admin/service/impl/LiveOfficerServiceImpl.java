package com.qiqilm.server.admin.service.impl;

import com.google.common.collect.Lists;
import com.qiqilm.server.admin.cache.ManageCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.domain.LiveOfficer;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LiveOfficerMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.ILiveOfficerService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LiveOfficerServiceImpl implements ILiveOfficerService {

    @Resource
    private LiveOfficerMapper liveOfficerMapper;
    @Resource
    private MemberInfoMapper memberInfoMapper;
    @Resource
    private ManageCacheUtil manageCacheUtil;

    /**
     * 查询房管管理 select live officer by id
     *
     * @param id 房管管理ID
     * @return 房管管理
     */
    @Override
    public LiveOfficer selectLiveOfficerById(String id) {
        return liveOfficerMapper.selectLiveOfficerById(id);
    }

    /**
     * 查询房管管理列表 Query liveOfficer management list
     *
     * @param liveOfficer 房管管理
     * @return 房管管理
     */
    @Override
    public List<LiveOfficer> selectLiveOfficerList(LiveOfficer liveOfficer) {
        List<LiveOfficer> liveOfficers = liveOfficerMapper.selectLiveOfficerList(liveOfficer);
        Set<String> puserIds = liveOfficers.stream().map(LiveOfficer::getPuserId).collect(Collectors.toSet());

        List<MemberInfo> memberInfos;
        if (Objects.isNull(LiveCenterConfig.me.getLiveSubAgents())) {
            memberInfos = memberInfoMapper.selectNikeNameById(puserIds);
        } else {
            if (puserIds.isEmpty()) {
                memberInfos = new ArrayList<>();
            } else {
                List<String> liveSubAgents = Arrays.asList(LiveCenterConfig.me.getLiveSubAgents());
                Set<String> liveSubAgentSet = liveSubAgents.stream().map(a -> LiveCenterConfig.me.getLiveSubAgentDbMain(a)).collect(Collectors.toSet());
                liveSubAgentSet.add(LiveCenterConfig.me.getLiveCenterDbMain());
                memberInfos = memberInfoMapper.selectAllDBNikeName(puserIds, liveSubAgentSet);
            }
        }
        for (MemberInfo memberInfo : memberInfos) {
            for (LiveOfficer officer : liveOfficers) {
                if (officer.getPuserId().equals(memberInfo.getId())) {
                    officer.setPuserName(memberInfo.getNickName());
                }
            }
        }
        return liveOfficers;
    }

    /**
     * 新增房管管理 Add liveOfficer management
     *
     * @param liveOfficer 房管管理
     * @return 结果
     */
    @Override
    public int insertLiveOfficer(LiveOfficer liveOfficer) {
        liveOfficer.setCtime(new Date());
        liveOfficer.setStatus(1L);
        if (liveOfficer.getHostId() != null && StringUtils.isNotBlank(liveOfficer.getPuserId())) {
            liveOfficer.setId(liveOfficer.getPuserId() + "-" + liveOfficer.getHostId());
            int getCountedId = liveOfficerMapper.countId(liveOfficer.getId());
            if (getCountedId > 0) {
                throw new BusinessException("记录已存在，请勿重复添加");
            }
        }
        manageCacheUtil.addManage(liveOfficer.getHostId(), liveOfficer.getPuserId());
        return liveOfficerMapper.insertLiveOfficer(liveOfficer);
    }

    /**
     * 批量删除房管管理 deleted by ids liveOfficer management
     *
     * @param ids 需要删除的房管管理ID liveOfficer ID to be deleted
     * @return 结果
     */
    @Override
    public int deleteLiveOfficerByIds(String[] ids) {
        int i = liveOfficerMapper.deleteLiveOfficerByIds(ids);
        if (i > 0) {
            for (String id : ids) {
                String[] split = id.split("-");
                manageCacheUtil.removeManage(Long.parseLong(split[1]), split[0]);
            }
        }
        return i;
    }

    /**
     * 删除房管管理信息 liveOfficer delete by id
     *
     * @param id 房管管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveOfficerById(String id) {
        int i = liveOfficerMapper.deleteLiveOfficerById(id);
        if (i > 0) {
            String[] split = id.split("-");
            manageCacheUtil.removeManage(Long.parseLong(split[1]), split[0]);
        }
        return i;
    }
}
