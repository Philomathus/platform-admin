package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveFamilyMapper;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.service.ILiveFamilyService;

/**
 * 家族Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LiveFamilyServiceImpl implements ILiveFamilyService {
    @Autowired
    private LiveFamilyMapper liveFamilyMapper;

    /**
     * 查询家族
     *
     * @param id 家族ID
     * @return 家族
     */
    @Override
    public LiveFamily selectLiveFamilyById(Long id) {
        return liveFamilyMapper.selectLiveFamilyById(id);
    }

    /**
     * 查询家族列表
     *
     * @param liveFamily 家族
     * @return 家族
     */
    @Override
    public List<LiveFamily> selectLiveFamilyList(LiveFamily liveFamily) {
        return liveFamilyMapper.selectLiveFamilyList(liveFamily);
    }

    /**
     * 新增家族
     *
     * @param liveFamily 家族
     * @return 结果
     */
    @Override
    public int insertLiveFamily(LiveFamily liveFamily) {
       // liveFamily.setCreateTime(DateUtils.getNowDate());
        return liveFamilyMapper.insertLiveFamily(liveFamily);
    }

    /**
     * 修改家族
     *
     * @param liveFamily 家族
     * @return 结果
     */
    @Override
    public int updateLiveFamily(LiveFamily liveFamily) {
        return liveFamilyMapper.updateLiveFamily(liveFamily);
    }

    /**
     * 批量删除家族
     *
     * @param ids 需要删除的家族ID
     * @return 结果
     */
    @Override
    public int deleteLiveFamilyByIds(Long[] ids) {
        return liveFamilyMapper.deleteLiveFamilyByIds(ids);
    }

    /**
     * 删除家族信息
     *
     * @param id 家族ID
     * @return 结果
     */
    @Override
    public int deleteLiveFamilyById(Long id) {
        return liveFamilyMapper.deleteLiveFamilyById(id);
    }
}
