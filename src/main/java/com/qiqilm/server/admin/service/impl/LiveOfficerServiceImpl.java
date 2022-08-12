package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LiveOfficer;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LiveOfficerMapper;
import com.qiqilm.server.admin.service.ILiveOfficerService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class LiveOfficerServiceImpl implements ILiveOfficerService {

    @Resource
    private LiveOfficerMapper liveOfficerMapper;

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
        List<LiveOfficer> listOffices = liveOfficerMapper.selectLiveOfficerList(liveOfficer);
        for(LiveOfficer getLiveOfficer : listOffices){
            getLiveOfficer.setStatus(1L);
        }
        return listOffices;
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
        if(liveOfficer.getHostId()!=null && StringUtils.isNotBlank(liveOfficer.getPuserId())){
            liveOfficer.setId(liveOfficer.getPuserId() + "-" + liveOfficer.getHostId());
            int getCountedId = liveOfficerMapper.countId(liveOfficer.getId());
            if(getCountedId > 0){
                throw new BusinessException("记录已存在，请勿重复添加");
            }
        }
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
        return liveOfficerMapper.deleteLiveOfficerByIds(ids);
    }

    /**
     * 删除房管管理信息 liveOfficer delete by id
     *
     * @param id 房管管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveOfficerById(String id) {
        return liveOfficerMapper.deleteLiveOfficerById(id);
    }
}
