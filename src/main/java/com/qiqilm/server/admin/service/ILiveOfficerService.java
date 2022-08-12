package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LiveOfficer;

import java.util.List;

public interface ILiveOfficerService {

    /**
     * 查询房管管理 select live officer by id
     *
     * @param id 房管管理ID
     * @return 房管管理
     */
    public LiveOfficer selectLiveOfficerById(String id);

    /**
     * 查询房管管理列表 select live officer as list
     *
     * @param liveOfficer 房管管理
     * @return 房管管理集合
     */
    public List<LiveOfficer> selectLiveOfficerList(LiveOfficer liveOfficer);

    /**
     * 新增房管管理  insert new live officer
     *
     * @param liveOfficer 房管管理
     * @return 结果
     */
    public int insertLiveOfficer(LiveOfficer liveOfficer);

    /**
     * 批量删除房管管理 delete multiple live officers
     *
     * @param ids 需要删除的房管管理ID
     * @return 结果
     */
    public int deleteLiveOfficerByIds(String[] ids );

    /**
     * 删除房管管理信息  delete live officer
     *
     * @param id 房管管理ID
     * @return 结果
     */
    public int deleteLiveOfficerById(String id);
}
