package com.qiqilm.server.admin.service.impl;

import java.util.List;
import java.util.Objects;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
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
    @Autowired
    private LiveUserMapper liveUserMapper;
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
    public AjaxResult insertLiveFamily(LiveFamily liveFamily) {
       // liveFamily.setCreateTime(DateUtils.getNowDate());
        String name = liveFamily.getName();
       LiveFamily getliveFamily= liveFamilyMapper.selectLiveFamilyName(name);
       if (Objects.nonNull(getliveFamily)){
           return   AjaxResult.success(liveFamily.getName()+"，家族已被创建");
       }
        Long userId = liveFamily.getUserId();
        LiveUser liveUser = liveUserMapper.selectLiveUserById(userId);
        if (Objects.isNull(liveUser)){
            return AjaxResult.success("主播不存在,无法创建家族");
        }else {
            Integer isBan = liveUser.getIsBan();
            Long familyId = liveUser.getFamilyId();
            Integer familyChieftain = liveUser.getFamilyChieftain();
        }

        liveFamilyMapper.insertLiveFamily(liveFamily);
        return null;
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
