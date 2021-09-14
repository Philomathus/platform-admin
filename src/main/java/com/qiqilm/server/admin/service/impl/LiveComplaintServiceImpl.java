package com.qiqilm.server.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveComplaintMapper;
import com.qiqilm.server.admin.domain.LiveComplaint;
import com.qiqilm.server.admin.service.ILiveComplaintService;

/**
 * 主播投诉记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-14
 */
@Service
public class LiveComplaintServiceImpl implements ILiveComplaintService {
    @Autowired
    private LiveComplaintMapper liveComplaintMapper;
    @Autowired
    private TokenService       tokenService;

    /**
     * 查询主播投诉记录
     *
     * @param id 主播投诉记录ID
     * @return 主播投诉记录
     */
    @Override
    public LiveComplaint selectLiveComplaintById(Long id) {
        return liveComplaintMapper.selectLiveComplaintById(id);
    }

    /**
     * 查询主播投诉记录列表
     *
     * @param liveComplaint 主播投诉记录
     * @return 主播投诉记录
     */
    @Override
    public List<LiveComplaint> selectLiveComplaintList(LiveComplaint liveComplaint) {
        return liveComplaintMapper.selectLiveComplaintList(liveComplaint);
    }

    /**
     * 新增主播投诉记录
     *
     * @param liveComplaint 主播投诉记录
     * @return 结果
     */
    @Override
    public int insertLiveComplaint(LiveComplaint liveComplaint) {
        liveComplaint.setCreateTime(DateUtils.getNowDate());
        return liveComplaintMapper.insertLiveComplaint(liveComplaint);
    }

    /**
     * 修改主播投诉记录
     *
     * @param liveComplaint 主播投诉记录
     * @return 结果
     */
    @Override
    public int updateLiveComplaint(LiveComplaint liveComplaint) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        liveComplaint.setApprover(userName);
        liveComplaint.setProcessingTime(new Date());
        return liveComplaintMapper.updateLiveComplaint(liveComplaint);
    }

    /**
     * 批量删除主播投诉记录
     *
     * @param ids 需要删除的主播投诉记录ID
     * @return 结果
     */
    @Override
    public int deleteLiveComplaintByIds(Long[] ids) {
        return liveComplaintMapper.deleteLiveComplaintByIds(ids);
    }

    /**
     * 删除主播投诉记录信息
     *
     * @param id 主播投诉记录ID
     * @return 结果
     */
    @Override
    public int deleteLiveComplaintById(Long id) {
        return liveComplaintMapper.deleteLiveComplaintById(id);
    }
}
